package product.review.domain.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review

@Repository
interface ReviewRepository : CoroutineCrudRepository<Review, Long> {

}