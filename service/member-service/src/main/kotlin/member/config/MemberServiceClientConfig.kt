package member.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class MemberServiceClientConfig{

    @Bean
    fun webClient() = WebClient.builder().build()
}