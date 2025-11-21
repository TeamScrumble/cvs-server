package security.token

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
    fun decodeToken(token: String): AuthPrincipal? {
        val payload = runCatching {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        }.getOrElse {
            return null
        }

        val memberId = payload.subject.toLongOrNull() ?: return null
        val type = payload.get("type", String::class.java) ?: return null
        val roles = payload.get("roles", List::class.java)?.filterIsInstance<String>()

        return AuthPrincipal(
            memberId = memberId,
            roles = roles?.toSet() ?: emptySet(),
            type = TokenType.valueOf(type),
        )
    }
}