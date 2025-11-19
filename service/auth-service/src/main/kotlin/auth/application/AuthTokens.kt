package auth.application

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)