package auth.infra.oauth

import auth.LoginRedirect
import auth.application.TokenService
import cache.CacheMemory
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import passport.Passport
import reactor.core.publisher.Mono
import security.passport.PassportProvider
import java.net.URI

@Component
class OAuth2SuccessHandler(
    private val tokenService: TokenService,
    private val passportProvider: PassportProvider,
    private val cacheMemory: CacheMemory
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        return mono {
            val authenticatedUser = authentication.principal as Oauth2AuthenticatedUser

            val passport = Passport(
                authId = authenticatedUser.authId,
                authProvider = authenticatedUser.provider.name,
                memberId = authenticatedUser.memberId,
                email = authenticatedUser.email,
                roles = authenticatedUser.roles,
                nickname = authenticatedUser.nickname,
            )
            val encodedPassport = passportProvider.encodePassport(passport)
            cacheMemory.set("Passport:" + authenticatedUser.memberId, encodedPassport)

            val ticket = tokenService.issueTicket(authenticatedUser.memberId)

            LoginRedirect.withTicket(ticket)
        }.flatMap { redirectUrl ->
            val response = webFilterExchange.exchange.response
            response.statusCode = HttpStatus.FOUND
            response.headers.location = URI.create(redirectUrl)
            response.setComplete()
        }
    }
}