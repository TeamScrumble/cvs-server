package product.review.application

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import passport.Passport
import review.ReviewAddApi
import review.ReviewGetApi

@Service
class ReviewFacade(
    private val reviewService: ReviewService,
    private val likeService: ReviewLikeService,
    private val scoreService: ReviewScoreService,
    private val imgService: ReviewImgService,
) {

    @Transactional
    suspend fun add(
        request: ReviewAddApi.Request
    ): Long {
        // todo 회원, 상품 검증
        val memberId = 1L
        val reviewId = reviewService.add(request, memberId)
        scoreService.addScores(reviewId, request.scores)

        return reviewId
    }

    suspend fun getReviewList(
        productId: Long,
        memberId: Long,
        page: Int,
        size: Int
    ): List<ReviewGetApi.Response> = coroutineScope {
        // 리뷰 목록 가져오기
        val reviews = reviewService.getList(productId, page, size)
        if (reviews.isEmpty()) return@coroutineScope emptyList()

        val reviewIds = reviews.map { it.id }

        val imagesDeferred = async { imgService.getImages(reviewIds) }
        val scoresDeferred = async { scoreService.getScores(reviewIds) }
        val likesDeferred  = async { likeService.getReviewCount(reviewIds) }
        val memberLikedDeferred  = async {
            likeService.countMemberLikedReviews(reviewIds, memberId)
        }

        val images = imagesDeferred.await()
        val scores = scoresDeferred.await()
        val likes = likesDeferred.await()
        val memberLiked = memberLikedDeferred.await()

        reviews.map { review ->
            ReviewGetApi.Response(
                reviewId = review.id,
                memberId = review.memberId,
                nickname = "닉네임",
                profileImage = "",
                lastModifiedAt = review.lastModifiedAt.toString(),
                rating = review.rating,
                content = review.content,
                likeCount = likes[review.id] ?: 0,
                isLikeByMe = memberLiked.contains(review.id),
                scores = scores[review.id] ?: emptyList(),
                imgList = images[review.id] ?: emptyList()
            )
        }
    }

    private suspend fun validateMember(passport: Passport) {
        // todo 회원 검증
    }


}