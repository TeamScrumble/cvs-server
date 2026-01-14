package member.domain.agreement

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AgreementRepository : CoroutineCrudRepository<Agreement, Long> {
    suspend fun findByIsActiveTrue(): Flow<Agreement>
}