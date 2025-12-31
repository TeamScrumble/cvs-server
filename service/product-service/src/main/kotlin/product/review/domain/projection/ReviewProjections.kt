package product.review.domain.projection

data class ReviewStatProjection(
    val optionId: Long,
    val count: Long
)

data class LikeCountProjection(
    val reviewId: Long,
    val count: Long
)
