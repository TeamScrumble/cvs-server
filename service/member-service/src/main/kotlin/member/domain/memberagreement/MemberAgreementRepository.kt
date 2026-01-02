package member.domain.memberagreement

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MemberAgreementRepository : CoroutineCrudRepository<MemberAgreement, Long> {
}