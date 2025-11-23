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

        fun buildFallbackError(message: String): Mono<ApiResponse<T>> =
            response.bodyToMono(String::class.java)
                .defaultIfEmpty("")
                .map {
                    ApiResponse.Error(
                        error = ErrorResponse(
                            code = BaseErrorCode.E_000.code,
                            description = message,
                        ),
                        status = status.value()
                    )
                }

        if (status.is2xxSuccessful) {
            // 2xx → Success<T>
            response.bodyToMono(successType)
                .map<ApiResponse<T>> { it }
                .onErrorResume { e ->
                    logger.warn("응답 파싱 실패(status=${status.value()}): ${e.message}", e)
                    buildFallbackError("응답 파싱 실패: ${e.message}")
                }
        } else {
            // 4xx / 5xx → Error<ErrorResponse>
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
                    buildFallbackError("에러 응답 비정상(HTTP $status): ${e.message}")
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