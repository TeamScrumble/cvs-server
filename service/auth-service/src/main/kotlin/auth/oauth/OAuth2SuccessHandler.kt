package auth.oauth

import auth.jwt.JwtService
import cache.CacheMemory
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import passport.Passport
import reactor.core.publisher.Mono
import security.passport.PassportProvider

@Component
class OAuth2SuccessHandler(
    private val jwtService: JwtService,
    private val passportProvider: PassportProvider,
    private val cacheMemory: CacheMemory
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val exchange = webFilterExchange.exchange
        val response = exchange.response

        val principal = authentication.principal as OAuth2User
        val provider = principal.attributes["provider"].toString()
        val providerId = principal.attributes["providerId"].toString()
        val email = principal.attributes["email"]?.toString()
        val name = principal.attributes["name"]?.toString()
        val subject = "$provider:$providerId"

        println(subject)

        val access = jwtService.issueAccessToken(
            subject,
            mapOf("email" to email, "name" to name)
        )
        val refresh = jwtService.issueRefreshToken(subject)

        println("attributes = ${principal.attributes}")
        println("{access:$access, refresh:$refresh}")

        val memberId = 1L
        val passport = Passport(memberId, setOf("user"), "nickname")
        val encodedPassport = passportProvider.encodePassport(passport)

        println(passport)
        println(encodedPassport)

        return mono {
            cacheMemory.set("Passport:${memberId}", encodedPassport)
        }.then(response.setComplete())

//        val redirectUrl =
//            "http://localhost:3000/auth/callback?access=$access&refresh=$refresh"
//
//        response.statusCode = HttpStatus.FOUND
//        response.headers.location = URI.create(redirectUrl)
    }
}