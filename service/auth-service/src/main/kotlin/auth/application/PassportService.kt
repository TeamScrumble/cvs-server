package auth.application

import auth.infra.cache.PassportCacheMemory
import error.exception.InternalServerException
import org.springframework.stereotype.Service
import passport.MemberRole.Companion.toRoleSet
import passport.Passport
import security.passport.PassportProvider

@Service
class PassportService(
    private val passportCacheMemory: PassportCacheMemory,
    private val passportProvider: PassportProvider,
) {

    suspend fun setPassport(authMember: AuthMember) {
        val passport = Passport(
            authId = authMember.authId,
            authProvider = authMember.provider.name,
            memberId = authMember.memberId,
            email = authMember.email,
            roles = authMember.roles.toRoleSet(),
            nickname = authMember.nickname,
        )
        val encodedPassport = passportProvider.encodePassport(passport)
        passportCacheMemory.setPassport(authMember.memberId, encodedPassport)
        passportCacheMemory.setSnapshot(authMember.memberId, encodedPassport)
    }

    suspend fun refresh(memberId: Long) {
        val snapshot = passportCacheMemory.getSnapshot(memberId)
            ?: throw InternalServerException("Passport Snapshot을 찾을 수 없습니다.")
        passportCacheMemory.setPassport(memberId, snapshot)
        passportCacheMemory.setSnapshot(memberId, snapshot)
    }
}