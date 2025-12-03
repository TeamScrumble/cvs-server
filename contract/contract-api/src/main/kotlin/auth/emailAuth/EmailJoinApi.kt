package auth.emailAuth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface EmailJoinApi {
    companion object {
        const val PATH = "/api/auth/email-auth/join"
    }
    @Documented(
        summary = "이메일 회원가입 API",
        description = "이메일 화원가입 API 입니다. 이메일 인증 완료 후 이 API를 호출해 주세요 (안하면 에러).<br/>" +
                "응답으로 반환되는 인증 토큰으로 자동 로그인 시켜주시면 됩니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun join(request: Request): ApiResponse<Response>

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