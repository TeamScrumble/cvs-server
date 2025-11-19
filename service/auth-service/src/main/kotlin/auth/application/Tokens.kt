package auth.application

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)