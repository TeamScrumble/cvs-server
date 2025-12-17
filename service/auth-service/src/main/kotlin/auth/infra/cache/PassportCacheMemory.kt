package auth.infra.cache

import auth.config.AuthServiceTokenProperties
import cache.CacheMemory
import org.springframework.stereotype.Component
import security.config.SecurityProperties

@Component
class PassportCacheMemory(
    private val cacheMemory: CacheMemory,
    private val tokenProperties: AuthServiceTokenProperties
) {

    private fun passportCacheKey(memberId: Long): String =
        VERIFICATION_CODE_CACHE_KEY_PREFIX + memberId

    suspend fun setPassport(
        memberId: Long,
        passport: String,
    ) {
        cacheMemory.set(
            key = passportCacheKey(memberId),
            value = passport,
            ttlMillis = tokenProperties.accessTokenExpires
        )
    }

    companion object {
        const val VERIFICATION_CODE_CACHE_KEY_PREFIX = "PASSPORT:"
    }
}