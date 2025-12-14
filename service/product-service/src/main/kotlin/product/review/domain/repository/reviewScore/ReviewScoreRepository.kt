package product.review.domain.repository.reviewScore

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewScore
import product.review.domain.projection.ReviewStatProjection

@Repository
interface ReviewScoreRepository :
    CoroutineCrudRepository<ReviewScore, Long>,
    ReviewScoreCustomRepository
{

    @Query(
        """
        SELECT ras.option_id AS optionId, COUNT(*) AS count 
        FROM review_aspect_score ras 
        JOIN review r ON ras.review_id = r.review_id 
        WHERE r.product_id = :productId AND r.is_deleted = 0 
        GROUP BY ras.option_id 
        """
    )
    suspend fun findStatsByProductId(productId: Long): List<ReviewStatProjection>

}