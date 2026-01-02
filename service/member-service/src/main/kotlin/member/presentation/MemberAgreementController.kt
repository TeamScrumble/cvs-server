package member.presentation

import ApiResponse
import member.application.MemberAgreementService
import member.memberagreement.MemberAgreementApi
import member.memberagreement.NeedAgreementApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberAgreementController(
    private val memberAgreementService: MemberAgreementService
) : MemberAgreementApi {

    @GetMapping(NeedAgreementApi.PATH)
    override suspend fun needAgreement(
        @RequestParam memberId: Long
    ): ApiResponse<NeedAgreementApi.Response> {
        val needAgreement = memberAgreementService.needAgreement(memberId)
        val response = NeedAgreementApi.Response(needAgreement)

        return ApiResponse.Success(response)
    }
}
