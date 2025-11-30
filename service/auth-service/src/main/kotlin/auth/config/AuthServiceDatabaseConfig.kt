package auth.config

import db.config.DatabaseSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthServiceDatabaseConfig {

    @Bean
    fun databaseScheme(
        props: AuthServiceProperties
    ) = DatabaseSchema(props.dbSchema)
}