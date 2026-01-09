package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ReviewSummaryGetApi {

    @Documented(
        summary = "(공용) 상품 리뷰 요약 조회 API",
        description = "(공용) 상품 리뷰에 대한 정보를 조회하는 API",
        response = Response::class
    )
    suspend fun getSummary(
        @Parameter(description = "상품 id", example = "1", `in` = ParameterIn.PATH)
        productId: Long
    ): ApiResponse<Response>

    @Documented(
        summary = "(로그인) 상품 리뷰 요약 조회 API",
        description = "(로그인) 공용 요약 + 리뷰 작성 가능 여부를 포함한 API",
        response = MeResponse::class
    )
    suspend fun getSummaryMe(
        passport: Passport,
        @Parameter(description = "상품 id", example = "1", `in` = ParameterIn.PATH)
        productId: Long
    ): ApiResponse<MeResponse>

    data class MeResponse(
        @Schema(description = "로그인한 사용자의 해당 상품 리뷰 작성 가능 여부", example = "false")
        val canWriteReview: Boolean,

        @Schema(description = "다음 작성 가능 날짜", example = "2026-01-07")
        val nextWritableDate: String,

        @Schema(description = "공용 리뷰 요약")
        val summary: Response
    )

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