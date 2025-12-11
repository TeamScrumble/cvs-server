package product.review.domain.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewScore

@Repository
interface ReviewScoreRepository : CoroutineCrudRepository<ReviewScore, Long> {
    suspend fun findAllByReviewIdIn(reviewIdList: List<Long>): List<ReviewScore>

}