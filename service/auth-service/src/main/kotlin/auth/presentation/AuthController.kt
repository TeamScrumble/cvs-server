package auth.presentation

import ApiResponse
import auth.AuthApi
import auth.LogoutApi
import auth.TokenExchangeApi
import auth.TokenReissueApi
import auth.application.AuthService
import auth.application.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import security.passport.RequestPassport

@RestController
class AuthController(
    private val tokenService: TokenService,
    private val authService: AuthService
) : AuthApi {

    @PostMapping(TokenReissueApi.PATH)
    override suspend fun reissue(
        @RequestHeader("X-Refresh-Token") refreshHeader: String
    ): ApiResponse<TokenReissueApi.Response> {
        val refreshToken = extractToken(refreshHeader)
        val reissuedTokens = tokenService.reissue(refreshToken)
        val response = TokenReissueApi.Response(reissuedTokens.accessToken, reissuedTokens.refreshToken)

        return ApiResponse.Success(response)
    }

    private fun extractToken(header: String): String {
        if (!header.startsWith("Bearer ")) {
            throw IllegalArgumentException("Invalid token format")
        }
        return header.substringAfter("Bearer ").trim()
    }

    @PostMapping(LogoutApi.PATH)
    override suspend fun logout(
        @RequestPassport passport: Passport
    ): ApiResponse<LogoutApi.Response> {
        authService.logout(passport)
        val response = LogoutApi.Response(true)

        return ApiResponse.Success(response)
    }

    @PostMapping(TokenExchangeApi.PATH)
    override suspend fun exchange(
        @RequestBody request: TokenExchangeApi.Request
    ): ApiResponse<TokenExchangeApi.Response> {
        val tokens = authService.exchange(request.ticket)
        val response = TokenExchangeApi.Response(tokens.accessToken, tokens.refreshToken)

        return ApiResponse.Success(response)
    }
}
