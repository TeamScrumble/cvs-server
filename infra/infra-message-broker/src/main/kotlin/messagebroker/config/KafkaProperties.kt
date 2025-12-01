package messagebroker.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "message-broker")
data class KafkaProperties(
    val url: String
)