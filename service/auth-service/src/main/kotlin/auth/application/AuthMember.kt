package auth.application

import auth.domain.auth.AuthProvider

data class AuthMember(
    val authId: Long,
    val provider: AuthProvider,
    val providerId: String,
    val memberId: Long,
    val roles: Set<String>,
    val email: String,
    val nickname: String,
)
