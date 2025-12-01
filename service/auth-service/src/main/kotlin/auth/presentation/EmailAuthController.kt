package auth.presentation

import ApiResponse
import auth.application.EmailAuthService
import auth.emailAuth.EmailAuthApi
import auth.emailAuth.SendVerificationCodeEmailApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailAuthController(
    private val emailAuthService: EmailAuthService
) : EmailAuthApi {

    @PostMapping(SendVerificationCodeEmailApi.PATH)
    override suspend fun sendVerificationCodeEmail(
        @RequestBody request: SendVerificationCodeEmailApi.Request
    ): ApiResponse<SendVerificationCodeEmailApi.Response> {
        emailAuthService.sendVerificationCodeEmail(request.email)
        val response = SendVerificationCodeEmailApi.Response(true)

        return ApiResponse.Success(response)
    }
}