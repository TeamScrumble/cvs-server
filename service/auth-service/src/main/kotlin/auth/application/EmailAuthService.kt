package auth.application

import auth.infra.cache.EmailVerifyCacheMemory
import auth.infra.mail.MailSender
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import java.security.SecureRandom
import kotlin.math.PI

@Service
class EmailAuthService(
    private val mailSender: MailSender,
    private val emailVerifyCacheMemory: EmailVerifyCacheMemory
) {
    suspend fun sendVerificationCodeEmail(email: String) {
        if (!isValidEmail(email)) {
            throw BusinessException(AuthErrorCode.A_004)
        }

        val verificationCode = SecureRandom()
            .nextInt(1_000_000)  // 0 ~ 999,999
            .toString()
            .padStart(6, '0')

        val mailSendResult = mailSender.send(email, "Verify your Email", verificationCode)
        if (!mailSendResult) {
            throw BusinessException(AuthErrorCode.A_003)
        }

        emailVerifyCacheMemory.setVerificationCode(email, verificationCode)
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(regex)
    }
}