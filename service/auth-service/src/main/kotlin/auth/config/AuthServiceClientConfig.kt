package auth.config

import member.MemberApi
import member.MemberApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class AuthServiceClientConfig{

    @Bean
    fun webClient() = WebClient.builder().build()

    @Bean
    fun memberApi(
        props: AuthServiceProperties,
        webClient: WebClient
    ): MemberApi {
        return MemberApiClient(
            host = props.gatewayHost,
            webClient = webClient
        )
    }
}