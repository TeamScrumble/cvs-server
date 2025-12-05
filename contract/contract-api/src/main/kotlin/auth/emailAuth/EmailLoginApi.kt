package auth.emailAuth

import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface EmailLoginApi {
    companion object {
        const val PATH = "/api/auth/email-auth/login"
    }
    @Documented(
        summary = "이메일 로그인 API",
        description = "이메일 로그인 API 입니다. <br/>" +
                "redirect:pyunpyun://auth/login/redirect?ticket=\${ticket} 으로 리다이렉트 됩니다.",
        request = Request::class,
    )
    suspend fun login(request: Request): String

    data class Request(
        @Schema(description = "이메일", example = "mobility42@gmail.com")
        val email: String,
        @Schema(description = "비밀번호", example = "qwer1234!")
        val password: String
    )
}