package review.like

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Review", description = "상품 리뷰 API")
interface ReviewLikeApi :
    ReviewLikeAddApi
{

    companion object {
        const val PATH = "/api/product/review/{reviewId}/like"
    }

    data class LikeResponse(
        @Schema(description = "내가 도움돼요를 눌렀는지 여부", example = "true")
        val liked: Boolean,

        @Schema(description = "리뷰의 도움돼요 총 개수", example = "14")
        val likeCount: Long
    )

}