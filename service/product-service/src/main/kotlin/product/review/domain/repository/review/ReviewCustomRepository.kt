package product.review.domain.repository.review

import product.review.domain.entity.Review
import review.ReviewListApi

interface ReviewCustomRepository {

    suspend fun findList(
        productId: Long,
        request: ReviewListApi.Request,
        offset: Int
    ): List<Review>

    suspend fun getLikeCount(reviewId: Long): Long

    suspend fun incrementLikeCount(reviewId: Long)

    suspend fun decrementLikeCount(reviewId: Long)

}