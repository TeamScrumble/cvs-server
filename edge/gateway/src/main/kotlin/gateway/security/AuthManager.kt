package gateway.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import security.token.TokenProvider

@Component
class AuthManager(
    private val tokenProvider: TokenProvider
) : ReactiveAuthenticationManager {

    override fun authenticate(
        authentication: Authentication
    ): Mono<Authentication> {
        val token = authentication.credentials as? String
            ?: return Mono.empty()

        return Mono.fromCallable {
            val principal = tokenProvider.decodeToken(token)
                ?: throw IllegalArgumentException("Invalid Token")

            UsernamePasswordAuthenticationToken(
                principal.memberId,
                token,
                principal.roles.map { SimpleGrantedAuthority(it) }
            )
        }
    }
}
