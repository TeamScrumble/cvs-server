package auth.application

import org.springframework.stereotype.Service

@Service
class EmailJoinFacadeService(
    private val emailAuthService: EmailAuthService,
    private val tokenService: TokenService
) {

    suspend fun join(
        email: String,
        password: String
    ): String {
        val memberId = emailAuthService.join(email, password)
        return tokenService.issueTicket(memberId)
    }
}
