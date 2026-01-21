package auth.infra.cache

import auth.config.AuthServiceTokenProperties
import cache.CacheMemory
import org.springframework.stereotype.Component

@Component
class PassportCacheMemory(
    private val cacheMemory: CacheMemory,
    private val tokenProperties: AuthServiceTokenProperties
) {

    private fun passportCacheKey(memberId: Long): String =
        PASSPORT_CACHE_KEY_PREFIX + memberId

    private fun passportSnapshotCacheKey(memberId: Long): String =
        PASSPORT_SNAPSHOT_CACHE_KEY_PREFIX + memberId

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

    suspend fun evictPassport(memberId: Long) {
        cacheMemory.evict(passportCacheKey(memberId))
    }

    suspend fun getSnapshot(memberId: Long): String? {
        return cacheMemory.get<String>(passportSnapshotCacheKey(memberId))
    }

    suspend fun setSnapshot(
        memberId: Long,
        passport: String,
    ) {
        cacheMemory.set(
            key = passportSnapshotCacheKey(memberId),
            value = passport,
        )
    }

    suspend fun evictSnapshot(memberId: Long) {
        cacheMemory.evict(passportSnapshotCacheKey(memberId))
    }

    companion object {
        const val PASSPORT_CACHE_KEY_PREFIX = "PASSPORT:"
        const val PASSPORT_SNAPSHOT_CACHE_KEY_PREFIX = "PASSPORT:SNAPSHOT:"
    }
}