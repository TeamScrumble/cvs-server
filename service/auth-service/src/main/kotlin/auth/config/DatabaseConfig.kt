package auth.config

import db.config.DatabaseScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    @Bean
    fun databaseScheme() = DatabaseScheme("cvs_auth")
}