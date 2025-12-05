package auth.presentation

import ApiResponse
import auth.LoginRedirect
import auth.application.EmailAuthService
import auth.application.EmailJoinFacadeService
import auth.application.EmailLoginFacadeService
import auth.emailAuth.EmailAuthApi
import auth.emailAuth.EmailExistsApi
import auth.emailAuth.EmailJoinApi
import auth.emailAuth.EmailLoginApi
import auth.emailAuth.SendVerificationCodeEmailApi
import auth.emailAuth.VerifyEmailApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailAuthController(
    private val emailAuthService: EmailAuthService,
    private val emailJoinFacadeService: EmailJoinFacadeService,
    private val emailLoginFacadeService: EmailLoginFacadeService
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
    ): String {
        val ticket = emailJoinFacadeService.join(request.email, request.password)
        return LoginRedirect.springRedirectWithTicket(ticket)
    }

    @PostMapping(EmailExistsApi.PATH)
    override suspend fun emailExits(
        @RequestBody request: EmailExistsApi.Request
    ): ApiResponse<EmailExistsApi.Response> {
        val exists = emailAuthService.emailExists(request.email)
        val response = EmailExistsApi.Response(exists)

        return ApiResponse.Success(response)
    }

    @PostMapping(EmailLoginApi.PATH)
    override suspend fun login(
        @RequestBody request: EmailLoginApi.Request
    ): String {
        val ticket = emailLoginFacadeService.login(request.email, request.password)
        return LoginRedirect.springRedirectWithTicket(ticket)
    }
}
