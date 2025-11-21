package db.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "db")
data class DatabaseProperties(
    val url: String,
    val username: String,
    val password: String
)