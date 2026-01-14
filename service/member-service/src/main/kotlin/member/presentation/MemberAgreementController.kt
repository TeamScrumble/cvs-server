package member.presentation

import ApiResponse
import member.application.MemberAgreementService
import member.memberagreement.MemberAgreeAPi
import member.memberagreement.MemberAgreementApi
import member.memberagreement.NeedAgreementApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import security.passport.RequestPassport

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

    @PostMapping(MemberAgreeAPi.PATH)
    override suspend fun agree(
        @RequestBody request: MemberAgreeAPi.Request,
        @RequestPassport passport: Passport
    ): ApiResponse<MemberAgreeAPi.Response> {
        memberAgreementService.agree(request, passport)
        val response = MemberAgreeAPi.Response(true)

        return ApiResponse.Success(response)
    }
}
