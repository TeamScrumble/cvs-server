package auth.application

import auth.domain.auth.AuthProvider
import org.springframework.stereotype.Service

@Service
class AuthService {

    suspend fun findOrInsertByProvider(
        providerKey: AuthProviderKey,
        email: String,
    ): AuthMember {
        // 1. provider 기반으로 auth 조회
        //      - 1.1 여기서 auth가 없으면
        //      - 1.2 새로운 member를 생성하고, auth 객체도 생성한다.
        // auth에 있는 memberId로 member-service 에서 조회 후 auth + member 정보를 반환

        // todo 실제 로직으로 수정 해야함

        return AuthMember(
            authId = 1L,
            provider = providerKey.provider,
            providerId = providerKey.providerId,
            memberId = 1L,
            roles = setOf("USER"),
            email = email,
            nickname = "nickname"
        )
    }
}