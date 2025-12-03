package auth.emailAuth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface EmailLoginApi {
    companion object {
        const val PATH = "/api/auth/email-auth/login"
    }
    @Documented(
        summary = "이메일 로그인 API",
        description = "이메일 로그인 API 입니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun login(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "이메일", example = "mobility42@gmail.com")
        val email: String,
        @Schema(description = "비밀번호", example = "qwer1234!")
        val password: String
    )

    data class Response(
        @Schema(description = "인증 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val accessToken: String,
        @Schema(description = "갱신 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val refreshToken: String
    )
}