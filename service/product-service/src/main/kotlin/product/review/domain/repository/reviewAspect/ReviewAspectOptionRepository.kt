package product.review.domain.repository.reviewAspect

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspectOption

@Repository
interface ReviewAspectOptionRepository :
    CoroutineCrudRepository<ReviewAspectOption, Long>,
    AspectOptionCustomRepository