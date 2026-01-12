package member.presentation

import ApiResponse
import kotlinx.coroutines.flow.toList
import member.agreement.AgreementApi
import member.agreement.AgreementListApi
import member.application.AgreementService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AgreementController(
    private val agreementService: AgreementService
) : AgreementApi {

    @GetMapping(AgreementListApi.PATH)
    override suspend fun findAll(): ApiResponse<AgreementListApi.Response> {
        val agreements = agreementService.findAllAgreements().toList()
        val response = AgreementListApi.Response(agreements)

        return ApiResponse.Success(response)
    }
}
