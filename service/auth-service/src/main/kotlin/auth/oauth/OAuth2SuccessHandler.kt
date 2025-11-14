package auth.oauth

import auth.jwt.JwtService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OAuth2SuccessHandler(
    private val jwtService: JwtService
) : ServerAuthenticationSuccessHandler { // Changed

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange, // Changed
        authentication: Authentication // Changed
    ): Mono<Void> { // Changed
        val exchange = webFilterExchange.exchange // Changed
        val response = exchange.response // Changed

        val principal = authentication.principal as OAuth2User
        val provider = principal.attributes["provider"].toString()
        val providerId = principal.attributes["providerId"].toString()
        val email = principal.attributes["email"]?.toString()
        val name = principal.attributes["name"]?.toString()
        val subject = "$provider:$providerId"

        val access = jwtService.issueAccessToken(
            subject,
            mapOf("email" to email, "name" to name)
        )
        val refresh = jwtService.issueRefreshToken(subject)

        println("attributes = ${principal.attributes}")
        println("{access:$access, refresh:$refresh}")

//        val redirectUrl =
//            "http://localhost:3000/auth/callback?access=$access&refresh=$refresh"
//
//        response.statusCode = HttpStatus.FOUND // 302 // Changed
//        response.headers.location = URI.create(redirectUrl) // Changed
        return response.setComplete() // Changed
    }
}