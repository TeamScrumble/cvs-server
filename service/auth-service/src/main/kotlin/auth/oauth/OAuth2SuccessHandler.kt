package cvs.auth.oauth

import cvs.auth.jwt.JwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.security.core.Authentication

@Component
class OAuth2SuccessHandler(
    private val jwtService: JwtService
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        req: HttpServletRequest,
        res: HttpServletResponse,
        auth: Authentication
    ) {
        val principal = auth.principal as org.springframework.security.oauth2.core.user.OAuth2User
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

        // 브라우저에 JSON 출력해서 확인
//        res.contentType = "application/json;charset=UTF-8"
//        res.writer.write(
//            """{"access":"$access","refresh":"$refresh"}"""
//        )

        println("attributes = ${principal.attributes}")
        println("{access:$access, refresh:$refresh}")


        // todo: url 추후 수정
//        val redirectUrl = "http://localhost:8080/auth/callback?access=$access&refresh=$refresh"
//        res.sendRedirect(redirectUrl)
    }

}