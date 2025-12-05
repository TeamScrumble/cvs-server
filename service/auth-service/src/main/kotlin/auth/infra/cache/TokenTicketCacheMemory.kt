package auth.infra.cache

import auth.application.AuthTokens
import cache.CacheMemory
import org.springframework.stereotype.Component

@Component
class TokenTicketCacheMemory(
    private val cacheMemory: CacheMemory
) {
    private fun key(ticket: String): String =
        TOKEN_TICKET_CACHE_KEY_PREFIX + ticket

    suspend fun set(
        ticket: String,
        authTokens: AuthTokens,
    ) {
        cacheMemory.set(
            key = key(ticket),
            value = authTokens,
            ttlMillis = ONE_MINUTE
        )
    }

    suspend fun get(ticket: String): AuthTokens? {
        return cacheMemory.get<AuthTokens>(key(ticket))
    }

    suspend fun evict(ticket: String) {
        cacheMemory.evict(key(ticket))
    }

    companion object {
        const val TOKEN_TICKET_CACHE_KEY_PREFIX = "TOKEN_TICKET:"
        const val ONE_MINUTE = 60_000L
    }
}