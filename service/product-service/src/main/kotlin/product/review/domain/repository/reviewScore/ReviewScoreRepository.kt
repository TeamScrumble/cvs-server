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

}