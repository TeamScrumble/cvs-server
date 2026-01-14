package member.memberagreement

import ApiResponse
import docs.Documented

interface NeedAgreementApi {

    companion object {
        const val PATH = "/api/member/internal/agreement/need"
    }

    @Documented(
        summary = "약관 목록 동의가 필요한지 조회 하는 API",
        description = "약관 동의를 추가로 받아야 하는지 여부를 조회하는 API 입니다.",
        response = Response::class,
    )
    suspend fun needAgreement(memberId: Long): ApiResponse<Response>

    data class Response(
        val needAgreement: Boolean
    )
}