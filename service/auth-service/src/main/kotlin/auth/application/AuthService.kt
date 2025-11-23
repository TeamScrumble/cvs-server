package auth.application

import auth.domain.auth.Auth
import auth.domain.auth.AuthRepository
import db.extension.upsert
import db.transactional.Transactional
import extension.getOrThrow
import member.MemberAddApi
import member.MemberApi
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val transactional: Transactional,
    private val authRepository: AuthRepository,
    private val memberApi: MemberApi
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
        val memberAddResponse = memberApi.add(memberAddRequest).getOrThrow {
            throw IllegalStateException()
        }
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
}