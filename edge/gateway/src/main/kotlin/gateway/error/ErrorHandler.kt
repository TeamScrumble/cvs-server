//package gateway.error
//
//import ApiResponse
//import com.fasterxml.jackson.databind.ObjectMapper
//import error.ErrorResponse
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
//import org.springframework.cloud.gateway.support.TimeoutException
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.stereotype.Component
//import org.springframework.web.server.MissingRequestValueException
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//
//@Component
//class ErrorHandler(
//    private val objectMapper: ObjectMapper
//) : ErrorWebExceptionHandler {
//
//    override fun handle(
//        exchange: ServerWebExchange,
//        exception: Throwable
//    ): Mono<Void> {
//        val status = when (exception) {
//            is MissingRequestValueException -> HttpStatus.BAD_REQUEST
//            is TimeoutException -> HttpStatus.GATEWAY_TIMEOUT
//            else -> HttpStatus.INTERNAL_SERVER_ERROR
//        }
//
//        val body = ApiResponse.error<Any>(
//            ErrorResponse(
//                code = "GATEWAY_ERROR",
//                description = exception.message ?: "gateway error",
//            ),
//            status = status.value(),
//        )
//
//        val bytes = objectMapper.writeValueAsBytes(body)
//
//        exchange.response.statusCode = status
//        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
//
//        return exchange.response.writeWith(
//            Mono.just(exchange.response.bufferFactory().wrap(bytes))
//        )
//    }
//}