package auth.infra.cache

import cache.CacheMemory
import org.springframework.stereotype.Component

@Component
class EmailVerifyCacheMemory(
    private val cacheMemory: CacheMemory
) {

    private fun verificationCodeKey(email: String): String =
        VERIFICATION_CODE_CACHE_KEY_PREFIX + email

    suspend fun setVerificationCode(
        email: String,
        verificationCode: String,
    ) {
        cacheMemory.set(
            key = verificationCodeKey(email),
            value = verificationCode,
            ttlMillis = 300_000L
        )
    }

    suspend fun getVerificationCode(email: String): String? {
        return cacheMemory.get<String>(verificationCodeKey(email))
    }

    private fun verifiedKey(email: String): String =
        EMAIL_VERIFIED_CACHE_KEY_PREFIX + email

    suspend fun setVerified(email: String, ) {
        cacheMemory.set(
            key = verifiedKey(email),
            value = true,
            ttlMillis = 600_000L
        )
    }

    suspend fun getVerified(email: String): Boolean? {
        return cacheMemory.get<Boolean>(verifiedKey(email))
    }

    companion object {
        const val VERIFICATION_CODE_CACHE_KEY_PREFIX = "EMAIL:VERIFICATION_CODE:"
        const val EMAIL_VERIFIED_CACHE_KEY_PREFIX = "EMAIL:VERIFIES:"
    }
}