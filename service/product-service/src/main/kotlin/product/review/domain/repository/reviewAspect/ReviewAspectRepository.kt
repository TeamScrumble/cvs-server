package product.review.domain.repository.reviewAspect

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspect

@Repository
interface ReviewAspectRepository :
    CoroutineCrudRepository<ReviewAspect, Long>,
    AspectCustomRepository
{
    @Query(
        """
        SELECT *
        FROM review_aspect
        ORDER BY aspect_id ASC
        """
    )
    suspend fun findAllOrderByIdAsc(): List<ReviewAspect>
}