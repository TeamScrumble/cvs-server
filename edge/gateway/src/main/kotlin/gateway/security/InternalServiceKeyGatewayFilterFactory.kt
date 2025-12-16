package gateway.security

import com.fasterxml.jackson.databind.ObjectMapper
import error.ErrorResponse
import error.errorcode.BaseErrorCode
import gateway.config.GatewayProperties
import internal.InternalApiServiceKeyHeader
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Component
class InternalServiceKeyGatewayFilterFactory(
    private val props: GatewayProperties,
    private val objectMapper: ObjectMapper
) : AbstractGatewayFilterFactory<InternalServiceKeyGatewayFilterFactory.Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            val providedKey = request.headers.getFirst(config.headerName)
                ?: run {
                    log.warn(
                        "Internal service key rejected: missing header. path={}, header={}",
                        request.path.value(),
                        config.headerName
                    )
                    return@GatewayFilter reject(exchange, config.rejectStatus)
                }

            val expectedKey = props.serviceKey // @NotBlank로 부팅 때 검증됨

            val valid = MessageDigest.isEqual(
                providedKey.toByteArray(StandardCharsets.UTF_8),
                expectedKey.toByteArray(StandardCharsets.UTF_8)
            )

            if (!valid) {
                log.warn(
                    "Internal service key rejected: invalid key. path={}, header={}",
                    request.path.value(),
                    config.headerName
                )
                return@GatewayFilter reject(exchange, config.rejectStatus)
            }

            return@GatewayFilter chain.filter(exchange)
        }
    }

    private fun reject(exchange: ServerWebExchange, status: HttpStatus): Mono<Void> {
        if (exchange.response.isCommitted) return Mono.empty()

        val errorCode = BaseErrorCode.E_003
        val body = ApiResponse.Error<Any>(
            ErrorResponse(
                code = errorCode.code,
                description = errorCode.description,
            ),
            status = status.value(),
        )

        val bytes = objectMapper.writeValueAsBytes(body)
        exchange.response.statusCode = status
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return exchange.response.writeWith(Mono.just(exchange.response.bufferFactory().wrap(bytes)))
    }

    data class Config(
        var headerName: String = InternalApiServiceKeyHeader.KEY,
        var rejectStatus: HttpStatus = HttpStatus.NOT_FOUND
    )
}