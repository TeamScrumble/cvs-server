package member.config

import db.config.DatabaseSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MemberServiceDatabaseConfig {

    @Bean
    fun databaseScheme(
        props: MemberServiceProperties
    ) = DatabaseSchema(props.dbSchema)
}