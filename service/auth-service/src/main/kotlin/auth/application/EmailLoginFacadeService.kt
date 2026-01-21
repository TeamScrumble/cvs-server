package auth.application

import org.springframework.stereotype.Component

@Component
class EmailLoginFacadeService(
    private val emailAuthService: EmailAuthService,
    private val tokenService: TokenService,
    private val passportService: PassportService,
) {

    suspend fun login(
        email: String,
        rawPassword: String
    ): String {
        val authMember = emailAuthService.login(email, rawPassword)
        passportService.setPassport(authMember)
        return tokenService.issueTicket(authMember.memberId, authMember.roles)
    }
}