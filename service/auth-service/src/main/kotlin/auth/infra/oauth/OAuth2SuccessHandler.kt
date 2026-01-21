package auth.infra.oauth

import auth.LoginRedirect
import auth.application.AuthMember
import auth.application.PassportService
import auth.application.TokenService
import auth.infra.cache.PassportCacheMemory
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import passport.MemberRole.Companion.toRoleSet
import passport.Passport
import reactor.core.publisher.Mono
import security.passport.PassportProvider
import java.net.URI

@Component
class OAuth2SuccessHandler(
    private val tokenService: TokenService,
    private val passportService: PassportService
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        return mono {
            val authenticatedUser = authentication.principal as Oauth2AuthenticatedUser

            val authMember = AuthMember(
                authId = authenticatedUser.authId,
                provider = authenticatedUser.provider,
                providerId = authenticatedUser.providerId,
                memberId = authenticatedUser.memberId,
                email = authenticatedUser.email,
                roles = authenticatedUser.roles,
                nickname = authenticatedUser.nickname,
            )

            passportService.setPassport(authMember)
            val ticket = tokenService.issueTicket(authenticatedUser.memberId, authenticatedUser.roles)

            LoginRedirect.withTicket(ticket)
        }.flatMap { redirectUrl ->
            val response = webFilterExchange.exchange.response
            response.statusCode = HttpStatus.FOUND
            response.headers.location = URI.create(redirectUrl)
            response.setComplete()
        }
    }
}