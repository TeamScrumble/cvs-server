package product.common.config

import internal.InternalApiServiceKeyHeader
import member.MemberApi
import member.MemberApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import product.common.client.MemberClient

@Configuration
class ProductServiceClientConfig(
    private val props: ProductServiceProperties
) {

    @Bean
    fun webClient() = WebClient.builder()
        .defaultHeader(InternalApiServiceKeyHeader.KEY, props.serviceKey)
        .build()

    @Bean
    fun memberApi(
        webClient: WebClient
    ): MemberApi {
        return MemberApiClient(
            host = props.gatewayHost,
            webClient = webClient
        )
    }

    @Bean
    fun memberServiceClient(memberApiClient: MemberApiClient): MemberClient {
        return MemberClient(memberApiClient)
    }
}