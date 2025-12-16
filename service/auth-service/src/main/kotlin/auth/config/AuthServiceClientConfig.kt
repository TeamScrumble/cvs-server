package auth.config

import internal.InternalApiServiceKeyHeader
import member.MemberApi
import member.MemberApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class AuthServiceClientConfig(
    private val props: AuthServiceProperties
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
}