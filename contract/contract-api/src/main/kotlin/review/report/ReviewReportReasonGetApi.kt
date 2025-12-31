package review.report

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ReviewReportReasonGetApi {

    companion object {
        const val PATH = "/api/product/review/report/reason"
    }

    @Documented(
        summary = "상품 리뷰 신고 사유 목록 조회 API",
        description = "신고 화면에 표시할 신고 사유 목록을 조회하는 API",
        response = Response::class
    )
    suspend fun getReasons(): ApiResponse<List<Response>>

    data class Response(
        @Schema(description = "신고 사유 코드", example = "IRRELEVANT")
        val reasonCode: String,

        @Schema(description = "신고 사유 텍스트", example = "상품과 무관한 내용 또는 이미지")
        val description: String
    )

}