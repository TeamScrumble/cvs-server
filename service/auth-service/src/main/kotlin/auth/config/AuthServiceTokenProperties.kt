package auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "token")
data class AuthServiceTokenProperties(
    val accessTokenExpires: Long,
    val refreshTokenExpires: Long,
)
