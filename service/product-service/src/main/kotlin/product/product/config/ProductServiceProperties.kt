package product.product.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
data class ProductServiceProperties(
    val gatewayHost: String,
    val dbSchema: String,
)
