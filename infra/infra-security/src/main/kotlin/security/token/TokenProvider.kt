package security.token

import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import security.config.SecurityProperties
import java.util.*

@Component
class TokenProvider(
    private val props: SecurityProperties
) {
    private val key = Keys.hmacShaKeyFor(props.secretKey.toByteArray())

    fun encodeToken(
        principal: AuthPrincipal,
        ttl: Long
    ): String {
        val now = Date()
        val expiry = Date(now.time + ttl)

        return Jwts.builder()
            .subject(principal.memberId.toString())
            .claim("type", principal.type.name)
            .apply {
                if (principal.type == TokenType.ACCESS) {
                    claim("roles", principal.roles)
                }
            }
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    @Suppress("UNCHECKED_CAST")
    fun decodeToken(token: String): AuthPrincipal {
        val payload = try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw BusinessException(AuthErrorCode.A_002)
        } catch (e: Exception) {
            throw BusinessException(AuthErrorCode.A_001)
        }

        val memberId = payload.subject.toLongOrNull()
            ?: throw BusinessException(AuthErrorCode.A_001)
        val type = payload.get("type", String::class.java)
            ?: throw BusinessException(AuthErrorCode.A_001)
        val roles = payload.get("roles", List::class.java)?.filterIsInstance<String>()

        return AuthPrincipal(
            memberId = memberId,
            roles = roles?.toSet() ?: emptySet(),
            type = TokenType.valueOf(type),
        )
    }
}