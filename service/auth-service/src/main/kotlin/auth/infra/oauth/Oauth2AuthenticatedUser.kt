package auth.infra.oauth

import auth.domain.auth.AuthProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class Oauth2AuthenticatedUser(
    val authId: Long,
    val provider: AuthProvider,
    val providerId: String,

    val memberId: Long,
    val roles: Set<String>,
    val email: String,
    val nickname: String,
) : OAuth2User {
    override fun getAttributes(): Map<String, Any> {
        return mapOf(
            "authId" to authId,
            "provider" to provider.name,
            "providerId" to providerId,
            "memberId" to memberId,
            "roles" to roles,
            "email" to email,
            "name" to nickname
        )
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptySet()
    }

    override fun getName(): String {
        return memberId.toString()
    }
}
