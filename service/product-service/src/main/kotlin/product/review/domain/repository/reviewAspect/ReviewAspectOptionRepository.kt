package product.review.domain.repository.reviewAspect

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspectOption

@Repository
interface ReviewAspectOptionRepository : CoroutineCrudRepository<ReviewAspectOption, Long> {

    @Query(
        """
        SELECT *
        FROM review_aspect_option
        WHERE option_id IN (:ids)
        ORDER BY display_order ASC
        """
    )
    suspend fun findAllByIdInOrderByDisplayOrderAsc(
        aspectIds: Iterable<Long>
    ): List<ReviewAspectOption>

    @Query(
        """
        SELECT * 
        FROM review_aspect_option 
        WHERE aspect_id IN (:aspectIds) 
        ORDER BY aspect_id ASC, display_order ASC 
        """
    )
    suspend fun findAllByAspectIdInOrderByAspectAndDisplay(
        aspectIds: Iterable<Long>
    ): List<ReviewAspectOption>

}