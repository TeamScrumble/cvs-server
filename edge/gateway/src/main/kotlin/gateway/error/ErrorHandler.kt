package gateway.error

import ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import error.ErrorResponse
import error.errorcode.AuthErrorCode
import error.errorcode.GatewayErrorCode
import error.errorcode.InternalServerErrorCode
import error.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.cloud.gateway.support.TimeoutException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.MissingRequestValueException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
@Order(-2)
class ErrorHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(
        exchange: ServerWebExchange,
        exception: Throwable
    ): Mono<Void> {
        val response = exchange.response
        if (response.isCommitted) {
            return Mono.error(exception)
        }

        val (errorCode, status) = when (exception) {
            is MissingRequestValueException -> GatewayErrorCode.G_001 to HttpStatus.BAD_REQUEST
            is TimeoutException -> GatewayErrorCode.G_002 to HttpStatus.GATEWAY_TIMEOUT
            is BusinessException -> {
                val status = when (exception.errorCode) {
                    AuthErrorCode.A_001, AuthErrorCode.A_002 -> HttpStatus.UNAUTHORIZED
                    else -> HttpStatus.INTERNAL_SERVER_ERROR
                }
                exception.errorCode to status
            }
            else -> InternalServerErrorCode to HttpStatus.INTERNAL_SERVER_ERROR
        }

        val body = ApiResponse.Error<Any>(
            ErrorResponse(
                code = errorCode.code,
                description = errorCode.description,
            ),
            status = status.value(),
        )

        val bytes = objectMapper.writeValueAsBytes(body)

        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)))
            .doOnError { log.error("Failed to write error response", it) }
    }
}