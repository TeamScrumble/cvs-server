package cvs.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expire}") private val accessExpire: Long,
    @Value("\${jwt.refresh-token-expire}") private val refreshExpire: Long,
    private val refreshRepository: RedisRefreshTokenRepository
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())
//    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256) // deprecated

    fun issueAccessToken(subject: String, claims: Map<String, Any?>): String {
        val now = Date()
        val expiry = Date(now.time + accessExpire)

        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun issueRefreshToken(subject: String): String {
        val now = Date()
        val expiry = Date(now.time + refreshExpire)

        val token = Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()

        refreshRepository.save(subject, token, refreshExpire)

        return token
    }

    fun reissue(refreshToken: String): Pair<String, String>? {
        val claims = parse(refreshToken)
        val subject = claims.subject

        // 토큰 확인
        val saved = refreshRepository.find(subject) ?: return null
        if (saved != refreshToken) return null

        // access 재발급
        val newAccess = issueAccessToken(subject, emptyMap())
        // refresh 재발급
        val newRefresh = issueRefreshToken(subject)

        return newAccess to newRefresh
    }

    fun parse(token: String) =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}