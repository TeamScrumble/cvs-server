package auth.domain.auth

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AuthRepository : CoroutineCrudRepository<Auth, Long> {
    suspend fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): Auth?
}
