package crawler.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
data class CrawlerServiceProperties(
    val gatewayHost: String,
    val dbSchema: String,
)
