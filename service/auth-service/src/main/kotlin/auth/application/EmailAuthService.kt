package auth.application

import auth.domain.auth.Auth
import auth.domain.auth.AuthProvider
import auth.domain.auth.AuthRepository
import auth.domain.emailauth.EmailAuth
import auth.domain.emailauth.EmailAuthRepository
import auth.infra.cache.EmailVerifyCacheMemory
import auth.infra.mail.MailSender
import db.transactional.Transactional
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import member.MemberAddApi
import member.MemberApi
import org.springframework.stereotype.Service
import security.password.PasswordEncoder
import java.security.SecureRandom

@Service
class EmailAuthService(
    private val transactional: Transactional,
    private val emailAuthRepository: EmailAuthRepository,
    private val authRepository: AuthRepository,
    private val mailSender: MailSender,
    private val emailVerifyCacheMemory: EmailVerifyCacheMemory,
    private val memberApi: MemberApi,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun sendVerificationCodeEmail(email: String) {
        validateEmail(email)

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

    suspend fun verify(
        email: String,
        verificationCode: String
    ) {
        validateEmail(email)
        validateVerificationCode(verificationCode)

        val savedCode = emailVerifyCacheMemory.getVerificationCode(email)
            ?: throw BusinessException(AuthErrorCode.A_005)

        if (savedCode != verificationCode) {
            throw BusinessException(AuthErrorCode.A_006)
        }

        emailVerifyCacheMemory.setVerified(email)
    }

    private fun validateVerificationCode(verificationCode: String) {
        val regex = "^[0-9]{6}$".toRegex()
        if (!verificationCode.matches(regex)) {
            throw BusinessException(AuthErrorCode.A_007)
        }
    }

    suspend fun join(
        email: String,
        password: String
    ) = transactional {
        validateEmail(email)
        validatePassword(password)

        emailVerifyCacheMemory.getVerified(email)
            ?: throw BusinessException(AuthErrorCode.A_008)

        if (emailAuthRepository.existsByEmail(email)) {
            throw BusinessException(AuthErrorCode.A_010)
        }

        val emailAuth = EmailAuth(
            email = email,
            encodedPassword = passwordEncoder.encode(password)
        )
        val savedEmailAuth = emailAuthRepository.save(emailAuth)

        val memberAddRequest = MemberAddApi.Request(email)
        val member = memberApi.add(memberAddRequest).getOrThrow()

        val auth = Auth(
            provider = AuthProvider.INTERNAL,
            providerId = savedEmailAuth.id.toString(),
            memberId = member.memberId
        )
        authRepository.save(auth)

        member.memberId
    }

    private fun validateEmail(email: String) {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(regex)) {
            throw BusinessException(AuthErrorCode.A_004)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length !in 8..32) {
            throw BusinessException(AuthErrorCode.A_009)
        }

        val hasLetter = { password.any { it.isLetter() } }
        val hasDigit = { password.any { it.isDigit() } }
        val hasSpecial = { password.any { it in "!@#$%^&*()_+-={}[]|:;\"'<>,.?/`~" } }

        val conditions = listOf(hasLetter, hasDigit, hasSpecial)
        val typeCount = conditions.count { it() }

        if (typeCount < 2) {
            throw BusinessException(AuthErrorCode.A_009)
        }
    }
}