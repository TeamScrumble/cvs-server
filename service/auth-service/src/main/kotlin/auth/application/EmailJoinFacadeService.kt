package auth.application

import org.springframework.stereotype.Service

@Service
class EmailJoinFacadeService(
    private val emailAuthService: EmailAuthService,
    private val tokenService: TokenService,
    private val passportService: PassportService
) {

    suspend fun join(
        email: String,
        password: String
    ): String {
        val authMember = emailAuthService.join(email, password)
        passportService.setPassport(authMember)
        return tokenService.issueTicket(authMember.memberId, authMember.roles)
    }
}
