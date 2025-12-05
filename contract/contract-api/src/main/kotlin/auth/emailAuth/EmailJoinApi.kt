package auth.emailAuth

import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface EmailJoinApi {
    companion object {
        const val PATH = "/api/auth/email-auth/join"
    }
    @Documented(
        summary = "이메일 회원가입 API",
        description = "이메일 화원가입 API 입니다. 이메일 인증 완료 후 이 API를 호출해 주세요 (안하면 에러) <br/>" +
                "redirect:pyunpyun://auth/login/redirect?ticket=\${ticket} 으로 리다이렉트 됩니다.",
        request = Request::class,
    )
    suspend fun join(request: Request): String

    data class Request(
        @Schema(description = "이메일", example = "mobility42@gmail.com")
        val email: String,
        @Schema(description = "비밀번호", example = "qwer1234!")
        val password: String
    )
}