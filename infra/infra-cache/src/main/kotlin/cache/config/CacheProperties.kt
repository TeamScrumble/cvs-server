package cache.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cache")
data class CacheProperties(
    val host: String,
    val port: Int,
    var password: String? = null,
    var database: Int = 0,
)
