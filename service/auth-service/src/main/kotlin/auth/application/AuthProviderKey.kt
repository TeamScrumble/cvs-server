package auth.application

import auth.domain.auth.AuthProvider

data class AuthProviderKey(
    val provider: AuthProvider,
    val providerId: String
)