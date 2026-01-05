package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.GetMapping
import passport.Passport

interface ReviewGetApi {

    @Documented(
        summary = "상품 리뷰 조회 API",
        description = "상품의 리뷰를 조회하는 API",
        response = Response::class
    )
    suspend fun get(
        passport: Passport,
        @Parameter(description = "상품 리뷰 id", example = "1", `in` = ParameterIn.PATH)
        reviewId: Long
    ): ApiResponse<Response>

    data class Response(
        @Schema(description = "리뷰 id", example = "1")
        val reviewId: Long,

        @Schema(description = "리뷰 작성자 id", example = "1")
        val memberId: Long,

        @Schema(description = "리뷰 작성자 닉네임", example = "사악한 펭귄")
        val nickname: String,

        @Schema(description = "리뷰 작성자 프로필 이미지", example = "https://imageurl.com")
        val profileImage: String,

        @Schema(description = "리뷰 마지막 수정 날짜", example = "2025-12-03T12:30:00")
        val lastModifiedAt: String,

        @Schema(description = "별점", example = "5")
        val rating: Int,

        @Schema(description = "리뷰 내용", example = "맛있어요~! 단짠단짠!")
        val content: String,

        @Schema(description = "도움돼요 수", example = "8")
        val likeCount: Int,

        @Schema(description = "현재 사용자가 해당 리뷰에 도움돼요를 눌렀는지 여부", example = "false")
        val isLikeByMe: Boolean,

        @Schema(description = "영수증 인증 여부", example = "false")
        val isReceipt: Boolean,

        @Schema(description = "평가", example = "품질/가성비/재구매의사")
        val scores: List<ScoreResponse> = emptyList(),

        @Schema(description = "리뷰 이미지 목록")
        val imgList: List<String> = emptyList()
    ){
        data class ScoreResponse(
            @Schema(description = "평가 항목 id", example = "1")
            val aspectId: Long,

            @Schema(description = "선택한 옵션 id", example = "1")
            val optionId: Long,

            @Schema(description = "평가 항목 문구", example = "품질")
            val aspectTitle: String,

            @Schema(description = "선택한 옵션 문구", example = "매우 좋아요")
            val optionName: String
        )
    }

}