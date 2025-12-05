package productservice.product.config

import db.config.DatabaseSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductServiceDatabaseConfig {

    @Bean
    fun databaseScheme(
        props: ProductServiceProperties
    ) = DatabaseSchema(props.dbSchema)
}