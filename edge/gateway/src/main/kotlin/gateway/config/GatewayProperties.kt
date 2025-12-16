package gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
data class GatewayProperties(
    val serviceKey: String
)
