package auth.application

import auth.domain.auth.Auth
import auth.domain.auth.AuthProvider
import auth.domain.auth.AuthRepository
import auth.domain.emailauth.EmailAuth
import auth.domain.emailauth.EmailAuthRepository
import auth.infra.cache.EmailVerifyCacheMemory
import auth.infra.mail.MailContent
import auth.infra.mail.MailSender
import db.transactional.Transactional
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import error.exception.InternalServerException
import extension.getOrThrow
import member.member.MemberAddApi
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
        val verificationCode = SecureRandom()
            .nextInt(1_000_000)  // 0 ~ 999,999
            .toString()
            .padStart(6, '0')

        val mailContent = MailContent.Template(
            to = email,
            subject = "이메일 인증 코드를 확인해주세요.",
            template = "mail-verification",
            "code" to verificationCode,
        )
        val mailSendResult = mailSender.send(mailContent)
        if (!mailSendResult) {
            throw BusinessException(AuthErrorCode.A_003)
        }

        emailVerifyCacheMemory.setVerificationCode(email, verificationCode)
    }

    suspend fun verify(
        email: String,
        verificationCode: String
    ) {
        val savedCode = emailVerifyCacheMemory.getVerificationCode(email)
            ?: throw BusinessException(AuthErrorCode.A_005)

        if (savedCode != verificationCode) {
            throw BusinessException(AuthErrorCode.A_006)
        }

        emailVerifyCacheMemory.setVerified(email)
    }

    suspend fun join(
        email: String,
        password: String
    ) = transactional {
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

    suspend fun emailExists(email: String): Boolean {
        return emailAuthRepository.existsByEmail(email)
    }

    suspend fun login(
        email: String,
        rawPassword: String
    ): Long {
        val emailAuth = emailAuthRepository.findByEmail(email)
            ?: throw BusinessException(AuthErrorCode.A_011)

        if (!passwordEncoder.matches(rawPassword, emailAuth.encodedPassword)) {
            throw BusinessException(AuthErrorCode.A_012)
        }

        val auth = authRepository.findByProviderAndProviderId(
            AuthProvider.INTERNAL,
            emailAuth.id.toString()
        ) ?: throw InternalServerException("이메일 로그인 정보는 있지만, 인증 정보가 없습니다. email : ${emailAuth.email}")

        return auth.memberId
    }
}