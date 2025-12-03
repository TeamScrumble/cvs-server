package auth.presentation

import ApiResponse
import auth.application.EmailAuthService
import auth.application.EmailJoinFacadeService
import auth.emailAuth.EmailAuthApi
import auth.emailAuth.EmailJoinApi
import auth.emailAuth.SendVerificationCodeEmailApi
import auth.emailAuth.VerifyEmailApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailAuthController(
    private val emailAuthService: EmailAuthService,
    private val emailJoinFacadeService: EmailJoinFacadeService
) : EmailAuthApi {

    @PostMapping(SendVerificationCodeEmailApi.PATH)
    override suspend fun sendVerificationCodeEmail(
        @RequestBody request: SendVerificationCodeEmailApi.Request
    ): ApiResponse<SendVerificationCodeEmailApi.Response> {
        emailAuthService.sendVerificationCodeEmail(request.email)
        val response = SendVerificationCodeEmailApi.Response(true)

        return ApiResponse.Success(response)
    }

    @PostMapping(VerifyEmailApi.PATH)
    override suspend fun verifyEmail(
        @RequestBody request: VerifyEmailApi.Request
    ): ApiResponse<VerifyEmailApi.Response> {
        emailAuthService.verify(request.email, request.verificationCode)
        val response = VerifyEmailApi.Response(true)

        return ApiResponse.Success(response)
    }

    @PostMapping(EmailJoinApi.PATH)
    override suspend fun join(
        @RequestBody request: EmailJoinApi.Request
    ): ApiResponse<EmailJoinApi.Response> {
        val tokens = emailJoinFacadeService.join(request.email, request.password)
        val response = EmailJoinApi.Response(tokens.accessToken, tokens.refreshToken)

        return ApiResponse.Success(response)
    }
}
