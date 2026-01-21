package auth.application

import auth.domain.auth.Auth
import auth.domain.auth.AuthRepository
import auth.infra.cache.PassportCacheMemory
import auth.infra.cache.RefreshTokenCacheMemory
import auth.infra.cache.TokenTicketCacheMemory
import db.extension.upsert
import db.transactional.Transactional
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import member.MemberAddApi
import member.MemberApi
import org.springframework.stereotype.Service
import passport.Passport

@Service
class AuthService(
    private val transactional: Transactional,
    private val authRepository: AuthRepository,
    private val memberApi: MemberApi,
    private val refreshTokenCacheMemory: RefreshTokenCacheMemory,
    private val tokenTicketCacheMemory: TokenTicketCacheMemory,
    private val passportCacheMemory: PassportCacheMemory,
) {

    suspend fun findOrInsertByProvider(
        providerKey: AuthProviderKey,
        email: String,
    ): AuthMember {
        val auth = authRepository.findByProviderAndProviderId(providerKey.provider, providerKey.providerId)
            ?: createNewMemberAndAuth(providerKey, email)

        val memberGetResponse = memberApi.get(auth.memberId).getOrThrow()

        return AuthMember(
            authId = auth.id,
            provider = auth.provider,
            providerId = auth.providerId,
            memberId = memberGetResponse.memberId,
            roles = memberGetResponse.roles,
            email = memberGetResponse.email,
            nickname = memberGetResponse.nickname
        )
    }

    private suspend fun createNewMemberAndAuth(
        providerKey: AuthProviderKey,
        email: String,
    ): Auth {
        val memberAddRequest = MemberAddApi.Request(
            email = email
        )
        val memberAddResponse = memberApi.add(memberAddRequest).getOrThrow()
        val memberId = memberAddResponse.memberId

        return transactional {
            val auth = Auth(
                provider = providerKey.provider,
                providerId = providerKey.providerId,
                memberId = memberId
            )

            authRepository.upsert(auth) {
                findByProviderAndProviderId(providerKey.provider, providerKey.providerId)
            }
        }
    }

    suspend fun logout(passport: Passport) {
        refreshTokenCacheMemory.evict(passport.memberId)
        passportCacheMemory.evictPassport(passport.memberId)
        passportCacheMemory.evictSnapshot(passport.memberId)
    }

    suspend fun exchange(ticket: String): AuthTokens {
        val tokens = tokenTicketCacheMemory.get(ticket)
            ?: throw BusinessException(AuthErrorCode.A_013)
        tokenTicketCacheMemory.evict(ticket)

        return tokens
    }
}