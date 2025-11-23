package extension

import ApiResponse
import error.ErrorResponse
import error.errorcode.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

val logger = LoggerFactory.getLogger("ContractApiClient")

inline fun <reified T> WebClient.RequestHeadersSpec<*>.exchangeToApiResponse(): Mono<ApiResponse<T>> {
    val successType = object : ParameterizedTypeReference<ApiResponse<T>>() {}
    val errorType   = object : ParameterizedTypeReference<ApiResponse<ErrorResponse>>() {}

    return exchangeToMono { response ->
        val status = response.statusCode()

        if (status.is2xxSuccessful) {
            response.bodyToMono(successType)
                .onErrorResume { e ->
                    logger.warn("응답 파싱 실패(status=${status.value()}): ${e.message}", e)

                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map {
                            ApiResponse.error<T>(
                                ErrorResponse(
                                    code = BaseErrorCode.E_000.code,
                                    description = "응답 파싱 실패: ${e.message}",
                                ),
                                status = status.value()
                            )
                        }
                }
        } else {
            response.bodyToMono(errorType)
                .map { apiError ->
                    val err = apiError.error ?: apiError.body!!
                    logger.info("Downstream 에러 응답(status=${status.value()}, code=${err.code}): ${err.description}")

                    ApiResponse.error<T>(err, status = status.value())
                }
                .onErrorResume { e ->
                    logger.warn("Downstream 에러 응답 형식 비정상(status=${status.value()}): ${e.message}", e)

                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map {
                            ApiResponse.error(
                                ErrorResponse(
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
            ApiResponse.error(
                ErrorResponse(
                    code = BaseErrorCode.E_000.code,
                    description = "연결 실패: ${e.message}",
                ),
                status = HttpStatus.SERVICE_UNAVAILABLE.value()
            )
        )
    }
}