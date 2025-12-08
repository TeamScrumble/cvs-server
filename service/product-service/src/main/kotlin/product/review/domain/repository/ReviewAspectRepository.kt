package product.review.domain.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspect

@Repository
interface ReviewAspectRepository : CoroutineCrudRepository<ReviewAspect, Long>