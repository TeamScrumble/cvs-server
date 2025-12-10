package crawler.config

import db.config.DatabaseSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CrawlerServiceDatabaseConfig {

    @Bean
    fun databaseScheme(
        props: CrawlerServiceProperties
    ) = DatabaseSchema(props.dbSchema)
}