package gateway.security

import cache.CacheMemory
import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import security.passport.PassportHeader

@Component
class PassportFilter(
    private val cacheMemory: CacheMemory
) : GlobalFilter {

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain
    ): Mono<Void> {
        val headers = exchange.request.headers

        if (PassportHeader.KEY in headers) {
            return chain.filter(exchange)
        }

        return exchange.getPrincipal<Authentication>()
            .flatMap { auth ->
                val memberId = auth.principal as? Long
                    ?: return@flatMap Mono.empty<String>()

                mono {
                    cacheMemory.get<String>("Passport:$memberId") ?: EMPTY_STRING
                }
            }
            .defaultIfEmpty(EMPTY_STRING)
            .flatMap { passport ->
                if (passport.isBlank()) {
                    return@flatMap chain.filter(exchange)
                }

                val mutatedReq = exchange.request.mutate()
                    .header(PassportHeader.KEY, passport)
                    .build()

                val mutatedExchange = exchange.mutate()
                    .request(mutatedReq)
                    .build()

                chain.filter(mutatedExchange)
            }
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}
