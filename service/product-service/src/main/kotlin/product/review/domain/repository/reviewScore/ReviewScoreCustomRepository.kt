package product.review.domain.repository.reviewScore

import product.review.domain.entity.ReviewScore
import product.review.domain.projection.ReviewStatProjection

interface ReviewScoreCustomRepository {
    suspend fun findAllByReviewIds(
        reviewIds: List<Long>
    ): List<ReviewScore>

    /*
    상품 리뷰들에서 선택된 옵션들의 count 구하기
     */
    suspend fun findStatsByProductId(
        productId: Long
    ): List<ReviewStatProjection>
}