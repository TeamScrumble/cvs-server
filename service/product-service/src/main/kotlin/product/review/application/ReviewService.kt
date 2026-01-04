package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import product.review.domain.entity.Review
import product.review.domain.repository.review.ReviewRepository
import review.ReviewAddApi
import review.ReviewListApi
import kotlin.math.round

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
) {

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
        request: ReviewListApi.Request
    ): List<Review> {
        val page = request.page
        val size = request.pageSize
        val offset = page * size

        return reviewRepository.findList(request, offset)
    }

    suspend fun getReview(reviewId: Long): Review {
        return reviewRepository
            .findById(reviewId)
            ?: throw BusinessException(ReviewErrorCode.R_001)
    }

    suspend fun getReviewCount(productId: Long): Long {
        return reviewRepository
            .countByProductIdAndIsDeletedFalse(productId)
    }

    suspend fun getReceiptCount(productId: Long): Long {
        return reviewRepository
            .countByProductIdAndIsReceiptTrueAndIsDeletedFalse(productId)
    }

    suspend fun getAvgRating(productId: Long): Double {
        val avg = reviewRepository
            .findAvgRatingByProductId(productId) ?: return 0.0

        return round(avg * 10) / 10.0
    }

    suspend fun existsById(reviewId: Long) =
        reviewRepository.existsById(reviewId)

    suspend fun getLikeCount(reviewId: Long): Long {
        return reviewRepository.getLikeCount(reviewId)
    }

    suspend fun incrementLikeCount(reviewId: Long) {
        reviewRepository.incrementLikeCount(reviewId)
    }

}