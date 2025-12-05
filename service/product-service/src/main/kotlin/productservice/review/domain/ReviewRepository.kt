package productservice.review.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewRepository : CoroutineCrudRepository<Review, Long> {

}