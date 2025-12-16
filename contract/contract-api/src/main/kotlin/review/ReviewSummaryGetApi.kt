package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.GetMapping

interface ReviewSummaryGetApi {

    @Documented(
        summary = "상품 리뷰 요약 조회 API",
        description = "상품 리뷰에 대한 정보를 조회하는 API",
        response = Response::class
    )
    @GetMapping("/summary")
    suspend fun getSummary(
        @Parameter(description = "상품 id", example = "1", `in` = ParameterIn.QUERY)
        productId: Long
    ): ApiResponse<Response>

    data class Response(
        @Schema(description = "전체 리뷰 개수", example = "347")
        val totalCount: Long,

        @Schema(description = "영수증 인증 리뷰 개수", example = "54")
        val receiptCount: Long,

        @Schema(description = "평균 별점", example = "4.8")
        val averageRating: Double,

        @Schema(description = "평가 정보")
        val aspects: List<AspectStat>
    ){
        data class AspectStat(
            @Schema(description = "평가 카테고리 id", example = "1")
            val aspectId: Long,

            @Schema(description = "평가 카테고리 제목", example = "품질")
            val title: String,

            @Schema(description = "평가 질문", example = "품질이 어떠셨나요?")
            val question: String,

            @Schema(description = "평가에 대한 옵션")
            val options: List<OptionStat>
        )

        data class OptionStat(
            @Schema(description = "평가 카테고리 옵션 id", example = "1")
            val optionId: Long,

            @Schema(description = "옵션 텍스트", example = "최고에요")
            val optionText: String,

            @Schema(description = "옵션 선택수", example = "10")
            val count: Long
        )
    }

}