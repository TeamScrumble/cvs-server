package auth.application

import org.springframework.stereotype.Component

@Component
class EmailLoginFacadeService(
    private val emailAuthService: EmailAuthService,
    private val tokenService: TokenService
) {

    suspend fun login(
        email: String,
        rawPassword: String
    ): AuthTokens {
        val memberId = emailAuthService.login(email, rawPassword)
        return tokenService.issueTokens(memberId)
    }
}