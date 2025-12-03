package auth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface TokenExchangeApi {
    companion object {
        const val PATH = "/api/auth/token/exchange"
    }
    @Documented(
        summary = "토큰 교환 API",
        description = "로그인 시 받은 ticket으로 토큰을 발급하는 API 입니다. <br/>." +
                "1분간 유효하며, 한번만 교환 가능 합니다.",
        response = Response::class,
    )
    suspend fun exchange(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "티켓", example = "5a69c321-51c5-4b20-84ea-b48b310592bc")
        val ticket: String,
    )

    data class Response(
        @Schema(description = "인증 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val accessToken: String,
        @Schema(description = "갱신 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val refreshToken: String
    )
}