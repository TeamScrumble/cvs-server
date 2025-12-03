package auth.domain.emailauth

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EmailAuthRepository : CoroutineCrudRepository<EmailAuth, Long> {
    suspend fun existsByEmail(email: String): Boolean
}