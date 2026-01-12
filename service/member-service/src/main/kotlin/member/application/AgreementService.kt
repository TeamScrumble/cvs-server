package member.application

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import member.agreement.AgreementListApi
import member.domain.agreement.AgreementRepository
import org.springframework.stereotype.Service

@Service
class AgreementService(
    private val agreementRepository: AgreementRepository
) {

    suspend fun findAllAgreements(): Flow<AgreementListApi.Response.Agreement> {
        return agreementRepository.findByIsActiveTrue().map {
            AgreementListApi.Response.Agreement(
                id = it.id,
                type = it.type.name,
                required = it.required,
                label = it.label,
                documentUrl = it.documentUrl,
                version = it.version
            )
        }
    }
}