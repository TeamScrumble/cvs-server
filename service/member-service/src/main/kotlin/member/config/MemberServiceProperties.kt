package member.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
data class MemberServiceProperties(
    val gatewayHost: String,
    val dbSchema: String,
)
