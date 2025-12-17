package crawler.config

import internal.InternalApiServiceKeyHeader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class CrawlerServiceClientConfig(
    private val props: CrawlerServiceProperties
) {

    @Bean
    fun webClient() = WebClient.builder()
        .defaultHeader(InternalApiServiceKeyHeader.KEY, props.serviceKey)
        .build()
}