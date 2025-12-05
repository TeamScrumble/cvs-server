package auth

import auth.emailAuth.VerifyEmailApi
import auth.emailAuth.EmailExistsApi
import auth.emailAuth.EmailJoinApi
import auth.emailAuth.EmailLoginApi
import auth.emailAuth.SendVerificationCodeEmailApi
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "EmailAuth", description = "이메일 로그인 API")
interface EmailAuthApi : SendVerificationCodeEmailApi, VerifyEmailApi, EmailJoinApi, EmailExistsApi, EmailLoginApi