package product.review.domain.repository.reviewReport

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewReport

@Repository
interface ReviewReportRepository : CoroutineCrudRepository<ReviewReport, Long> {
}