package auth.emailAuth

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "EmailAuth", description = "이메일 로그인 API")
interface EmailAuthApi : SendVerificationCodeEmailApi, VerifyEmailApi, EmailJoinApi, EmailExistsApi, EmailLoginApi