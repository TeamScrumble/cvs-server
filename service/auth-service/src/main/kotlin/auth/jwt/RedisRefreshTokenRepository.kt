package auth.jwt

import cache.CacheMemory
import org.springframework.stereotype.Repository

@Repository
class RedisRefreshTokenRepository(
    private val cacheMemory: CacheMemory
) {

    suspend fun save(subject: String, refreshToken: String, expireMillis: Long) {
        cacheMemory.set("refresh:$subject", refreshToken, expireMillis)
    }

    suspend fun find(subject: String): String? {
        return cacheMemory.get("refresh:$subject")
    }

    suspend fun delete(subject: String) {
        cacheMemory.evict("refresh:$subject")
    }
}
