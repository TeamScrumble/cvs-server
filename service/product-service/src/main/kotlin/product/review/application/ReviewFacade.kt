package product.review.application

import db.transactional.Transactional
import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService
import product.product.application.service.ProductService
import review.*

@Service
class ReviewFacade(
    private val transactional: Transactional,
    private val memberValidService: MemberValidService,
    private val reviewService: ReviewService,
    private val likeService: ReviewLikeService,
    private val scoreService: ReviewScoreService,
    private val imgService: ReviewImgService,
    private val productService: ProductService,
    private val aspectService: ReviewAspectService
) {

    suspend fun add(
        passport: Passport,
        request: ReviewAddApi.Request
    ): Long = transactional {
        memberValidService.validateMember(passport)
        if (!productService.existsById(request.productId)) {
            throw BusinessException(ReviewErrorCode.R_007)
        }

        val memberId = passport.memberId
        val reviewId = reviewService.add(request, memberId)
        // 평가 저장
        scoreService.addScores(reviewId, request.scores)
        // 이미지 저장
        imgService.addImages(reviewId, request.images)

        reviewId
    }

    suspend fun getReviewList(
        passport: Passport,
        request: ReviewListApi.Request
    ): List<ReviewGetApi.Response> = coroutineScope {
        memberValidService.validateMember(passport)
        val memberId = passport.memberId

        // 리뷰 목록 가져오기
        val reviews = reviewService.getList(request)
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

    suspend fun getSummary(
        productId: Long
    ): ReviewSummaryGetApi.Response = supervisorScope{
        val totalCount = async { reviewService.getReviewCount(productId) }
        val receiptCount = async { reviewService.getReceiptCount(productId) }
        val avgRating = async { reviewService.getAvgRating(productId) }
        val aspects = async { scoreService.getAspectStatsForSummary(productId) }

        ReviewSummaryGetApi.Response(
            totalCount = totalCount.await(),
            receiptCount = receiptCount.await(),
            averageRating = avgRating.await(),
            aspects = aspects.await()
        )
    }

    suspend fun getAspectInfo(): List<ReviewAspectGetApi.Response> {
        return aspectService.getAspectInfo()
    }


}