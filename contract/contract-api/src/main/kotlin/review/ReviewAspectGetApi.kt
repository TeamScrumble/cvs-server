package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ReviewAspectGetApi {

    @Documented(
        summary = "리뷰 평가 질문, 옵션 조회 API",
        description = "리뷰 평가 질문과 질문에 맞는 답변 옵션을 조회 API",
        response = ReviewAspectGetApi.Response::class
    )
    suspend fun getAspectInfo(): ApiResponse<List<ReviewAspectGetApi.Response>>

    data class Response(
        @Schema(description = "평가 카테고리 id", example = "1")
        val aspectId: Long,

        @Schema(description = "평가 제목", example = "품질")
        val aspectTitle: String,

        @Schema(description = "평가 질문", example = "품질이 어떠셨나요?")
        val aspectQuestion: String,

        @Schema(description = "평가 옵션")
        val options: List<Option>
    ) {
        data class Option(
            @Schema(description = "평가 옵션 id", example = "1")
            val optionId: Long,

            @Schema(description = "옵션 텍스트", example = "최고에요")
            val optionText: String,

            @Schema(description = "화면에 보여질 순서", example = "1")
            val displayOrder: Int
        )
    }
}