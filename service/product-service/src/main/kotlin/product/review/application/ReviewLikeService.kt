package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.repository.reviewLike.ReviewLikeRepository

@Service
class ReviewLikeService(
    private val likeRepository: ReviewLikeRepository
) {

    suspend fun getReviewCount(
        reviewIds: List<Long>
    ): Map<Long, Int> {
        if (reviewIds.isEmpty()) return emptyMap()

        return likeRepository
            .countLikesByReviewIds(reviewIds)
    }

    suspend fun countMemberLikedReviews(
        reviewIds: List<Long>,
        memberId: Long?
    ): Set<Long> {
        if (memberId == null || reviewIds.isEmpty()) {
            return emptySet()
        }

        return likeRepository
            .findMemberLikedReviewIds(reviewIds, memberId)
    }

}