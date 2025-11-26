package extension

import ApiResponse
import error.ErrorResponse
import error.errorcode.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

inline fun <reified T> WebClient.RequestHeadersSpec<*>.exchangeToApiResponse(): Mono<ApiResponse<T>> {
    val logger = LoggerFactory.getLogger(T::class.java)

    val successType = object : ParameterizedTypeReference<ApiResponse.Success<T>>() {}
    val errorType   = object : ParameterizedTypeReference<ApiResponse.Error<ErrorResponse>>() {}

    return exchangeToMono<ApiResponse<T>> { response ->
        val status = response.statusCode()

        if (status.is2xxSuccessful) {
            response.bodyToMono(successType)
                .map<ApiResponse<T>> { it }
                .onErrorResume { e ->
                    logger.warn("응답 파싱 실패(status=${status.value()}): ${e.message}", e)

                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map<ApiResponse<T>> {
                            ApiResponse.Error(
                                error = ErrorResponse(
                                    code = BaseErrorCode.E_000.code,
                                    description = "응답 파싱 실패: ${e.message}",
                                ),
                                status = status.value()
                            )
                        }
                }
        } else {
            response.bodyToMono(errorType)
                .map<ApiResponse<T>> { apiError ->
                    logger.info(
                        "Downstream 에러 응답(status=${status.value()}, code=${apiError.error.code}): ${apiError.error.description}"
                    )

                    ApiResponse.Error(
                        error = apiError.error,
                        status = apiError.status
                    )
                }
                .onErrorResume { e ->
                    logger.warn("Downstream 에러 응답 형식 비정상(status=${status.value()}): ${e.message}", e)

                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map<ApiResponse<T>> {
                            ApiResponse.Error(
                                error = ErrorResponse(
                                    code = BaseErrorCode.E_000.code,
                                    description = "에러 응답 비정상(HTTP $status): ${e.message}",
                                ),
                                status = status.value()
                            )
                        }
                }
        }

    }.onErrorResume { e ->
        logger.error("Downstream 연결 실패: ${e.message}", e)

        Mono.just(
            ApiResponse.Error(
                error = ErrorResponse(
                    code = BaseErrorCode.E_000.code,
                    description = "연결 실패: ${e.message}",
                ),
                status = HttpStatus.SERVICE_UNAVAILABLE.value()
            )
        )
    }
}
