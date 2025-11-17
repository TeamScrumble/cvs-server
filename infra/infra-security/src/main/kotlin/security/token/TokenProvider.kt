package security.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import security.config.SecurityProperties
import java.util.Date

@Component
class TokenProvider(
    private val props: SecurityProperties
) {
    private val key = Keys.hmacShaKeyFor(props.secretKey.toByteArray())

    fun encodeToken(memberPrincipal: AuthPrincipal): String {
        val now = Date()
        val expiry = Date(now.time + props.accessTokenExpires)

        return Jwts.builder()
            .subject(memberPrincipal.memberId.toString())
            .claim("roles", memberPrincipal.roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    @Suppress("UNCHECKED_CAST")
    fun decodeToken(token: String): AuthPrincipal {
        val payload = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val memberId = payload.subject.toLongOrNull()
            ?: throw IllegalArgumentException("Invalid Token")
        val roles = payload["roles"] as? List<String>
            ?: throw IllegalArgumentException("Invalid Token")

        return AuthPrincipal(memberId, roles.toSet())
    }
}