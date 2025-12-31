package product.review.domain.repository.reviewImage

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewImg

@Repository
interface ReviewImgRepository :
    CoroutineCrudRepository<ReviewImg, Long>,
    ReviewImgCustomRepository