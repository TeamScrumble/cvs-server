package product.review.domain.repository.reviewImage

import product.review.domain.entity.ReviewImg

interface ReviewImgCustomRepository {
    suspend fun findAllByReviewIds(reviewIds: List<Long>): List<ReviewImg>
}