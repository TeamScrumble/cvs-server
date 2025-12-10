package auth.infra.cache

import auth.config.AuthServiceTokenProperties
import cache.CacheMemory
import org.springframework.stereotype.Component

@Component
class RefreshTokenCacheMemory(
    private val cacheMemory: CacheMemory,
    private val tokenProperties: AuthServiceTokenProperties,
) {

    private fun key(memberId: Long): String =
        REFRESH_TOKEN_CACHE_KEY_PREFIX + memberId

    suspend fun set(
        memberId: Long,
        refreshToken: String,
        ttlMillis: Long? = null,
    ) {
        cacheMemory.set(
            key = key(memberId),
            value = refreshToken,
            ttlMillis = ttlMillis ?: tokenProperties.refreshTokenExpires
        )
    }

    suspend fun get(memberId: Long): String? {
        return cacheMemory.get<String>(key(memberId))
    }

    suspend fun evict(memberId: Long) {
        cacheMemory.evict(key(memberId))
    }

    companion object {
        const val REFRESH_TOKEN_CACHE_KEY_PREFIX = "RefreshToken:"
    }
}