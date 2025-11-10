package cvs.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("crawler")
internal data class CrawlerProperties(
    val uri: String,
    val secret: String
)