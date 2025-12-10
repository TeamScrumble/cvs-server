package db.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan
class DatabaseConnectionConfig {

    @Bean
    fun connectionFactory(
        props: DatabaseProperties,
        scheme: DatabaseSchema
    ): ConnectionFactory {
        val connectionUrl = "${props.url}/${scheme.value}"
        return ConnectionFactoryBuilder
            .withUrl(connectionUrl)
            .username(props.username)
            .password(props.password)
            .build()
    }
}