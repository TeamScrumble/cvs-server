package auth.emailAuth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

interface SendVerificationCodeEmailApi {
    companion object {
        const val PATH = "/api/auth/email-auth/verification-code"
    }
    @Documented(
        summary = "이메일 인증번호 전송 API",
        description = "이메일로 인증번호를 전송합니다. 인증 코드는 5분간 유효합니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun sendVerificationCodeEmail(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "인증 코드를 전송할 이메일", example = "mobility42@gmail.com")
        @field:Email
        val email: String
    )

    data class Response(
        @Schema(description = "성공 여부", example = "true")
        val success: Boolean,
    )
}