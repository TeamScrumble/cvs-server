package member.config

import db.config.DatabaseSchema
import io.r2dbc.spi.ConnectionFactory
import member.infra.db.converter.RoleSetToStringConverter
import member.infra.db.converter.StringToRoleSetConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.dialect.R2dbcDialect

@Configuration
class MemberServiceDatabaseConfig {

    @Bean
    fun databaseScheme(
        props: MemberServiceProperties
    ) = DatabaseSchema(props.dbSchema)

    @Bean
    fun r2dbcCustomConversions(
        connectionFactory: ConnectionFactory,
    ): R2dbcCustomConversions {
        val dialect: R2dbcDialect = DialectResolver.getDialect(connectionFactory)

        val converters = listOf(
            RoleSetToStringConverter(),
            StringToRoleSetConverter(),
        )

        return R2dbcCustomConversions.of(dialect, converters)
    }
}