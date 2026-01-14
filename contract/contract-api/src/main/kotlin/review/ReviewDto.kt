package review

import io.swagger.v3.oas.annotations.media.Schema

data class ReviewDto(
    @Schema(description = "리뷰 id", example = "1")
    val reviewId: Long,

    @Schema(description = "리뷰 작성자 id", example = "1")
    val memberId: Long,

    @Schema(description = "리뷰 작성자 닉네임", example = "사악한 펭귄")
    val nickname: String,

    @Schema(description = "리뷰 작성자 프로필 이미지", example = "https://imageurl.com")
    val profileImage: String,

    @Schema(description = "리뷰 작성 날짜", example = "2025-12-03T12:30:00")
    val createdAt: String,

    @Schema(description = "별점", example = "5")
    val rating: Int,

    @Schema(description = "리뷰 내용", example = "맛있어요~! 단짠단짠!")
    val content: String,

    @Schema(description = "도움돼요 수", example = "8")
    val likeCount: Long,

    @Schema(description = "현재 사용자가 해당 리뷰에 도움돼요를 눌렀는지 여부", example = "false")
    val isLikeByMe: Boolean,

    @Schema(description = "영수증 인증 여부", example = "false")
    val isReceipt: Boolean,

    @Schema(description = "평가")
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