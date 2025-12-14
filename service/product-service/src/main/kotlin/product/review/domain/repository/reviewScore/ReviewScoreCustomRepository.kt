package product.review.domain.repository.reviewScore

import product.review.domain.entity.ReviewScore

interface ReviewScoreCustomRepository {
    suspend fun findAllByReviewIds(
        reviewIds: List<Long>
    ): List<ReviewScore>
}