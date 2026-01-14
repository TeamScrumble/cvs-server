package member.agreement

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface AgreementListApi {

    companion object {
        const val PATH = "/api/member/agreement"
    }

    @Documented(
        summary = "약관 목록 조회 API",
        description = "약관 동의 시 체크해야 하는 목록을 조회하는 API 입니다. <br/>" +
                "type - [ACTIVITY_PUSH(활동 푸쉬)) | MARKETING_PUSH(마케팅 알림)] ",
        response = Response::class,
    )
    suspend fun findAll(): ApiResponse<Response>

    data class Response(
        @Schema(description = "약관 목록")
        val agreements: List<Agreement>
    ) {
        data class Agreement(
            @Schema(description = "아이디", example = "1")
            val id: Long,
            @Schema(description = "약관 타입", example = "ACTIVITY_PUSH")
            val type: String,
            @Schema(description = "필수 여부", example = "true")
            val required: Boolean,
            @Schema(description = "라벨", example = "활동 푸쉬")
            val label: String,
            @Schema(description = "약관 url", example = "example.com")
            val documentUrl: String,
            @Schema(description = "버전정보", example = "V1")
            val version: String,
        )
    }
}