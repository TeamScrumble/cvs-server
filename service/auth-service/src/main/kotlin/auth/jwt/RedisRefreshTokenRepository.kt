package cvs.auth.jwt

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRefreshTokenRepository(
    private val redis: StringRedisTemplate
) {

    fun save(subject: String, refreshToken: String, expireMillis: Long) {
        redis
            .opsForValue()
            .set(
                "refresh:$subject",
                refreshToken,
                expireMillis,
                TimeUnit.MILLISECONDS
            )
    }

    fun find(subject: String): String? {
        return redis.opsForValue().get("refresh:$subject")
    }

    fun delete(subject: String) {
        redis.delete("refresh:$subject")
    }

}