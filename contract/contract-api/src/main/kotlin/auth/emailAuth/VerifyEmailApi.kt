package auth.emailAuth

import ApiResponse
import auth.emailAuth.field.Email
import auth.emailAuth.field.VerificationCode
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface VerifyEmailApi {
    companion object {
        const val PATH = "/api/auth/email-auth/verify"
    }
    @Documented(
        summary = "이메일 인증 API",
        description = "전송받은 인증코드로 이메일을 인증합니다. 인증 여부는 10분간 유효합니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun verifyEmail(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "인증 코드를 전송 받은 이메일", example = "mobility42@gmail.com")
        @field:Email
        val email: String,
        @Schema(description = "이메일로 전송 받은 인증 코드", example = "41520")
        @field:VerificationCode
        val verificationCode: String
    )

    data class Response(
        @Schema(description = "성공 여부", example = "true")
        val success: Boolean,
    )
}