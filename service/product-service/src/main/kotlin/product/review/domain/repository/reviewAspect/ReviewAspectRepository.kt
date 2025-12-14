package product.review.domain.repository.reviewAspect

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspect

@Repository
interface ReviewAspectRepository : CoroutineCrudRepository<ReviewAspect, Long> {
    @Query(
        """
        SELECT * 
        FROM review_aspect 
        WHERE aspect_id IN (:ids) 
        ORDER BY aspect_id ASC
        """
    )
    suspend fun findAllByIdInOrderByIdAsc(
        ids: Iterable<Long>
    ): List<ReviewAspect>

}