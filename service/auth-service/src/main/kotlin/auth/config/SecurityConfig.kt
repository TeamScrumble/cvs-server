package auth.config

import auth.oauth.CustomOAuth2UserService
import auth.oauth.OAuth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
//    private val jwtAuthFilter: JwtAuthFilter, // WebFlux용 filter로 바꿔야 함 (WebFilter)
) {

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors { }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/auth/**", "/oauth2/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2.authenticationSuccessHandler(oAuth2SuccessHandler)
            }
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}