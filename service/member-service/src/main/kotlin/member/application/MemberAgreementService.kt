package member.application

import db.transactional.Transactional
import error.errorcode.MemberErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.flow.toList
import member.domain.agreement.AgreementType
import member.domain.member.MemberRepository
import member.domain.memberagreement.MemberAgreement
import member.domain.memberagreement.MemberAgreementRepository
import member.memberagreement.MemberAgreeAPi
import org.springframework.stereotype.Service
import passport.Passport
import java.time.LocalDateTime

@Service
class MemberAgreementService(
    private val transactional: Transactional,
    private val memberAgreementRepository: MemberAgreementRepository,
    private val memberRepository: MemberRepository
) {
    suspend fun needAgreement(memberId: Long): Boolean {
        val agreementTypes = AgreementType.entries.map { it.name }.toSet()
        val memberAgreements = memberAgreementRepository.findAllByMemberId(memberId).toList()

        return memberAgreements.map { it.agreementType }.toSet() != agreementTypes
    }

    suspend fun agree(
        request: MemberAgreeAPi.Request,
        passport: Passport
    ) = transactional {
        memberRepository.findById(passport.memberId)
            ?: throw BusinessException(MemberErrorCode.M_001)

        val memberAgreements = request.agreements.map {
            MemberAgreement(
                agreementType = AgreementType.valueOf(it.agreementType),
                agreed = it.agree,
                agreedAt = LocalDateTime.now(),
                memberId = passport.memberId
            )
        }

        memberAgreementRepository.saveAll(memberAgreements).toList()
    }
}
