package gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class GatewaySecurityConfig {

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        authManager: ReactiveAuthenticationManager,
        authConverter: ServerAuthenticationConverter
    ): SecurityWebFilterChain {

        val filter = AuthenticationWebFilter(authManager).apply {
            setServerAuthenticationConverter(authConverter)
        }

        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authenticationManager(authManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange {
                it.pathMatchers("/**").permitAll()
                    .anyExchange().permitAll()
            }
            .build()
    }
}