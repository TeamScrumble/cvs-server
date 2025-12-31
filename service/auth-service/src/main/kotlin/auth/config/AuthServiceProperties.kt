package auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
data class AuthServiceProperties(
    val gatewayHost: String,
    val dbSchema: String,
    val serviceKey: String
)
