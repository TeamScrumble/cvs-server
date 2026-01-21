package auth.application

import org.springframework.stereotype.Component

@Component
class TokenReissueFacadeService(
    private val tokenService: TokenService,
    private val passportService: PassportService,
) {

    suspend fun reissue(refreshToken: String): AuthTokens {
        val principal = tokenService.decodeRefreshToken(refreshToken)
        passportService.refresh(principal.memberId)
        return tokenService.issueTokens(principal.memberId, principal.roles)
    }
}