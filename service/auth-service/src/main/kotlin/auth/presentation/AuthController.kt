package auth.presentation

import ApiResponse
import auth.AuthApi
import auth.TokenReissueApi
import auth.application.TokenService
import of
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val tokenService: TokenService
) : AuthApi {

    @PostMapping(TokenReissueApi.PATH)
    override suspend fun reissue(
        @RequestHeader("X-Refresh-Token") refreshHeader: String
    ): ApiResponse<TokenReissueApi.Response> {
        val refreshToken = refreshHeader.removePrefix("Bearer ").trim()
        val reissuedTokens = tokenService.reissue(refreshToken)
        val response = TokenReissueApi.Response(reissuedTokens.accessToken, reissuedTokens.refreshToken)

        return ApiResponse.of(response)
    }
}