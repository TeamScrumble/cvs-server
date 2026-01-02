package member.application

import kotlinx.coroutines.flow.toList
import member.domain.agreement.AgreementType
import member.domain.memberagreement.MemberAgreementRepository
import org.springframework.stereotype.Service

@Service
class MemberAgreementService(
    private val memberAgreementRepository: MemberAgreementRepository,
) {
    suspend fun needAgreement(memberId: Long): Boolean {
        val agreementTypes = AgreementType.entries.map { it.name }.toSet()
        val memberAgreements = memberAgreementRepository.findAllByMemberId(memberId).toList()

        return memberAgreements.map { it.agreementType }.toSet() != agreementTypes
    }
}
