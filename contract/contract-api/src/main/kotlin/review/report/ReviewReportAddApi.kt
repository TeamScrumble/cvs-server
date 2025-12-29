package review.report

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

interface ReviewReportAddApi {

    companion object {
        const val PATH = "/api/product/review/{reviewId}/report"
    }

    @Documented(
        summary = "상품 리뷰 신고 등록 API",
        description = "상품 리뷰 신고를 등록하는 API",
        request = Request::class,
        response = Response::class
    )
    suspend fun reportAdd(
        @Parameter(description = "신고 대상 리뷰 id", `in` = ParameterIn.PATH)
        reviewId: Long,
        request: Request
    ): ApiResponse<Response>

    data class Request(
        @Schema(description = "신고 사유 코드", example = "IRRELEVANT")
        val reasonCode: String,

        @Schema(description = "신고 내용", example = "상품이 아닌 판매처에 대한 후기입니다.")
        @field:Size(max = 500)
        val content: String
    )

    data class Response(
        @Schema(description = "생성된 신고 id", example = "1")
        val reportId: Long
    )

}