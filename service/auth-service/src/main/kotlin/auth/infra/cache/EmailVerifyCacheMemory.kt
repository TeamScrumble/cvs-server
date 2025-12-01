package auth.infra.cache

import cache.CacheMemory
import org.springframework.stereotype.Component

@Component
class EmailVerifyCacheMemory(
    private val cacheMemory: CacheMemory
) {

    private fun key(email: String): String =
        VERIFICATION_CODE_CACHE_KEY_PREFIX + email

    suspend fun setVerificationCode(
        email: String,
        verificationCode: String,
    ) {
        cacheMemory.set(
            key = key(email),
            value = verificationCode,
            ttlMillis = 300_000L
        )
    }

    suspend fun getVerificationCode(email: String): String? {
        return cacheMemory.get<String>(key(email))
    }

    companion object {
        const val VERIFICATION_CODE_CACHE_KEY_PREFIX = "EMAIL:VERIFICATION_CODE:"
    }
}