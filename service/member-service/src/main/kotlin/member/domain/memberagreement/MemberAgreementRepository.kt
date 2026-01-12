package member.domain.memberagreement

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MemberAgreementRepository : CoroutineCrudRepository<MemberAgreement, Long> {
    suspend fun findAllByMemberId(memberId: Long): Flow<MemberAgreement>
}