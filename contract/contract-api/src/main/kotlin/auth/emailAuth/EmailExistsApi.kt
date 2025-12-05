package auth.emailAuth

import ApiResponse
import auth.emailAuth.field.Email
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface EmailExistsApi {
    companion object {
        const val PATH = "/api/auth/email-auth/email-exists"
    }
    @Documented(
        summary = "이메일 중복 확인 API",
        description = "존재하는 이메일이 있는지 확인하는 API 입니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun emailExits(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "이메일", example = "mobility42@gmail.com")
        @field:Email
        val email: String
    )

    data class Response(
        @Schema(description = "존재 여부 (false 이면 회원가입 가능)", example = "false")
        val exists: Boolean
    )
}