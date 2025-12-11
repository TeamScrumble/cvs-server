package product.review.domain.repository

import java.time.LocalDateTime

data class ReviewSortingStatProjection(
    val reviewId: Long,
    val likeCount: Long,
    val hasMedia: Boolean,
    val createdAt: LocalDateTime
)

data class ReviewStatProjection(
    val optionId: Long,
    val count: Long
)

data class LikeCountProjection(
    val reviewId: Long,
    val count: Long
)
