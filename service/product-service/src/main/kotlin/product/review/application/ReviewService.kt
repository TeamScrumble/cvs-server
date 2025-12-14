package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import product.review.domain.entity.Review
import product.review.domain.repository.review.ReviewRepository
import review.ReviewAddApi

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
) {

    @Transactional
    suspend fun add(
        request: ReviewAddApi.Request,
        memberId: Long,
    ): Long {
        val review = Review.create(
            productId = request.productId,
            memberId = memberId,
            rating = request.rating,
            content = request.content,
            isReceipt = request.isReceipt
        )

        return reviewRepository.save(review).id
    }

    suspend fun getList(
        productId: Long,
        page: Int, size: Int
    ): List<Review> {
        val offset = page * size

        return reviewRepository
            .findAllByProductIdAndIsDeletedFalse(
                productId = productId,
                offset = offset,
                size = size
            )
    }

    suspend fun getReview(reviewId: Long): Review {
        return reviewRepository
            .findById(reviewId)
            ?: throw BusinessException(ReviewErrorCode.R_001)
    }

}