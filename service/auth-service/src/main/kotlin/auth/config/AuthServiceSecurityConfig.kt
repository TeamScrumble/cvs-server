package auth.config

import auth.infra.oauth.OAuth2SuccessHandler
import auth.infra.oauth.Oauth2FailureHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class AuthServiceSecurityConfig(
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val oauth2FailureHandler: Oauth2FailureHandler
) {

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors { }
            .authorizeExchange { exchanges ->
                exchanges.anyExchange().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2.authenticationSuccessHandler(oAuth2SuccessHandler)
                oauth2.authenticationFailureHandler(oauth2FailureHandler)
            }
            .build()
    }
}