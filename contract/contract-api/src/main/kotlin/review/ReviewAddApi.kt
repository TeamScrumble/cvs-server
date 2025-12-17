package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import passport.Passport

interface ReviewAddApi {

    @Documented(
        summary = "상품 리뷰 등록 API",
        description = "상품 리뷰를 등록하는 API",
        request = Request::class,
        response = Response::class
    )
    suspend fun add(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "리뷰를 등록할 상품 id", example = "1")
        @field:Positive
        val productId: Long,

        @Schema(description = "상품 만족도 별점(1~5)", example = "5")
        @field:Min(1)
        @field:Max(5)
        val rating: Int,

        @Schema(description = "상품 리뷰(10~500)", example = "짱짱 맛있어요~!!bb")
        @field:NotBlank
        @field:Size(min = 10, max = 500)
        val content: String,

        @Schema(description = "상품 리뷰 영수증 인증 여부", example = "false")
        val isReceipt: Boolean,

        @Schema(description = "리뷰 평가 항목별 선택 옵션")
        val scores: List<ScoreRequest> = emptyList()
    ) {
        data class ScoreRequest(
            @Schema(description = "평가 항목 id", example = "1")
            val aspectId: Long,

            @Schema(description = "선택한 옵션 id", example = "2")
            val optionId: Long
        )
    }

    data class Response(
        @Schema(description = "생성된 리뷰 id", example = "1")
        val reviewId: Long
    )
}