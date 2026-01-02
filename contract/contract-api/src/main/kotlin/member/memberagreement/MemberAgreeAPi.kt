package member.memberagreement

import ApiResponse
import docs.Documented
import passport.Passport

interface MemberAgreeAPi {

    companion object {
        const val PATH = "/api/member/agreement/agree"
    }

    @Documented(
        summary = "약관 동의 API",
        description = "약관 동의 동작을 수행하는 API 입니다.",
        request = Request::class,
        response = Response::class,
    )
    suspend fun agree(
        request: Request,
        passport: Passport
    ): ApiResponse<Response>

    data class Request(
        val agreements: List<Agreement>
    ) {
        data class Agreement(
            val agreementType: String,
            val agree: Boolean
        )
    }

    data class Response(
        val success: Boolean
    )
}