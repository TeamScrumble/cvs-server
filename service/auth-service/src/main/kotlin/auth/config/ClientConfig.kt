package auth.config

import member.MemberApi
import member.MemberApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ClientConfig{

    @Bean
    fun webClient() = WebClient.builder().build()

    @Bean
    fun memberApi(
        webClient: WebClient
    ): MemberApi {
        return MemberApiClient(
            host = "localhost:8080",
            webClient = webClient
        )
    }
}