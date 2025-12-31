package product.review.domain.repository.reviewLike

interface ReviewLikeCustomRepository {
    suspend fun countLikesByReviewIds(reviewIds: List<Long>): Map<Long, Int>
    suspend fun findMemberLikedReviewIds(
        reviewIds: List<Long>,
        memberId: Long
    ): Set<Long>

}