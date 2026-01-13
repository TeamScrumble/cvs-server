package product.review.application

import db.transactional.Transactional
import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import member.MemberListApi
import org.springframework.stereotype.Service
import passport.Passport
import product.common.client.MemberClient
import product.common.valid.MemberValidService
import product.product.application.service.ProductService
import product.review.domain.entity.Review
import review.*
import review.like.ReviewLikeApi

@Service
class ReviewFacade(
    private val transactional: Transactional,
    private val memberValidService: MemberValidService,
    private val memberApiClient: MemberClient,
    private val reviewService: ReviewService,
    private val likeService: ReviewLikeService,
    private val scoreService: ReviewScoreService,
    private val imgService: ReviewImgService,
    private val productService: ProductService,
    private val aspectService: ReviewAspectService,
    private val reviewWritePolicyService: ReviewWritePolicyService
) {

    suspend fun add(
        passport: Passport,
        request: ReviewAddApi.Request,
        productId: Long
    ): Long = transactional {
        memberValidService.validateMember(passport)
        if (!productService.existsById(productId)) {
            throw BusinessException(ReviewErrorCode.R_007)
        }

        val memberId = passport.memberId

        // 리뷰 작성 가능 여부 재검증(1개월 이후부터 작성 가능)
        val eligibility = reviewWritePolicyService.getEligibility(productId, memberId)
        if (!eligibility.canWrite) {
            throw BusinessException(ReviewErrorCode.R_015)
        }

        val reviewId = reviewService.add(request, memberId, productId)
        // 평가 저장
        scoreService.addScores(reviewId, request.scores)
        // 이미지 저장
        imgService.addImages(reviewId, request.images)

        reviewId
    }

    suspend fun getReviewList(
        passport: Passport,
        request: ReviewListApi.Request,
        productId: Long
    ): List<ReviewGetApi.Response> = coroutineScope {
        memberValidService.validateMember(passport)
        val memberId = passport.memberId

        // 리뷰 목록 가져오기
        val reviews = reviewService.getList(productId, request)
        if (reviews.isEmpty()) return@coroutineScope emptyList()

        val reviewIds = reviews.map { it.id }
        val writerMemberIds = reviews.map { it.memberId }

        val data = fetchAssembleData(
            reviewIds = reviewIds,
            writerMemberIds = writerMemberIds,
            memberId = memberId
        )

        reviews.map { toResponse(it, data) }
    }

    suspend fun getReview(
        passport: Passport,
        reviewId: Long
    ): ReviewGetApi.Response = coroutineScope {
        memberValidService.validateMember(passport)
        val memberId = passport.memberId
        val review = reviewService.getReview(reviewId)

        val reviewIds = listOf(reviewId)
        val writerMemberIds = listOf(review.memberId)

        val data = fetchAssembleData(
            reviewIds = reviewIds,
            writerMemberIds = writerMemberIds,
            memberId = memberId
        )

        toResponse(review, data)
    }

    suspend fun delete(
        passport: Passport,
        reviewId: Long
    ): Long = transactional {
        memberValidService.validateMember(passport)

        val review = reviewService.getReview(reviewId)
        if (review.memberId != passport.memberId) {
            throw BusinessException(ReviewErrorCode.R_013)
        }

        reviewService.deleteReview(reviewId)

        reviewId
    }

    suspend fun getSummary(
        productId: Long
    ): ReviewSummaryGetApi.Summary = coroutineScope{
        val totalCount = async { reviewService.getReviewCount(productId) }
        val receiptCount = async { reviewService.getReceiptCount(productId) }
        val avgRating = async { reviewService.getAvgRating(productId) }
        val aspects = async { scoreService.getAspectStatsForSummary(productId) }

        ReviewSummaryGetApi.Summary(
            totalCount = totalCount.await(),
            receiptCount = receiptCount.await(),
            averageRating = avgRating.await(),
            aspects = aspects.await()
        )
    }

    suspend fun getSummaryUnified(
        passport: Passport?,
        productId: Long
    ): ReviewSummaryGetApi.Response = coroutineScope {
        val summaryDeferred = async { getSummary(productId) }

        val eligibilityDeferred = if (passport != null) {
            async {
                memberValidService.validateMember(passport)
                reviewWritePolicyService.getEligibility(productId, passport.memberId)
            }
        } else {
            null
        }

        val summary = summaryDeferred.await()
        val eligibility = eligibilityDeferred?.await()

        ReviewSummaryGetApi.Response(
            canWriteReview = eligibility?.canWrite,
            nextWritableDate = eligibility?.nextWritableDate.toString(),
            summary = summary
        )
    }

    suspend fun getAspectInfo(): List<ReviewAspectGetApi.Response> {
        return aspectService.getAspectInfo()
    }

    private data class ReviewAssembleData(
        val images: Map<Long, List<String>>,
        val scores: Map<Long, List<ReviewGetApi.Response.ScoreResponse>>,
        val memberLiked: Set<Long>,
        val writerMemberMap: Map<Long, MemberListApi.Response.Member>
    )

    private suspend fun fetchAssembleData(
        reviewIds: List<Long>,
        writerMemberIds: List<Long>,
        memberId: Long
    ): ReviewAssembleData = coroutineScope {
        val imagesDeferred = async { imgService.getImages(reviewIds) }
        val scoresDeferred = async { scoreService.getScores(reviewIds) }
        val memberLikedDeferred = async { likeService.countMemberLikedReviews(reviewIds, memberId) }
        val writerMemberMapDeferred = async { memberApiClient.getMemberMap(writerMemberIds) }

        ReviewAssembleData(
            images = imagesDeferred.await(),
            scores = scoresDeferred.await(),
            memberLiked = memberLikedDeferred.await(),
            writerMemberMap = writerMemberMapDeferred.await()
        )
    }

    private fun toResponse(
        review: Review,
        data: ReviewAssembleData
    ): ReviewGetApi.Response {
        val member = data.writerMemberMap[review.memberId]

        return ReviewGetApi.Response(
            reviewId = review.id,
            memberId = review.memberId,
            nickname = member?.nickname ?: "unknown",
            profileImage = member?.profileImage ?: "",
            lastModifiedAt = review.lastModifiedAt.toString(),
            rating = review.rating,
            content = review.content,
            likeCount = review.likeCount,
            isLikeByMe = data.memberLiked.contains(review.id),
            isReceipt = review.isReceipt,
            scores = data.scores[review.id] ?: emptyList(),
            imgList = data.images[review.id] ?: emptyList()
        )
    }

    suspend fun addReviewLike(
        passport: Passport,
        reviewId: Long
    ): ReviewLikeApi.LikeResponse = transactional {
        validateLikeAction(passport, reviewId)

        // 해당 리뷰에 도움돼요 카운트 증가
        val inserted = likeService.add(reviewId, passport.memberId)
        if (inserted) {
            reviewService.incrementLikeCount(reviewId)
        }
        // 도움돼요 수
        val likeCount = reviewService.getLikeCount(reviewId)

        ReviewLikeApi.LikeResponse(
            liked = true,
            likeCount = likeCount
        )
    }

    suspend fun removeReviewLike(
        passport: Passport,
        reviewId: Long
    ): ReviewLikeApi.LikeResponse = transactional {
        validateLikeAction(passport, reviewId)

        // 해당 리뷰에 도움돼요 카운트 감소
        val deleted = likeService.remove(reviewId, passport.memberId)
        if (deleted) {
            reviewService.decrementLikeCount(reviewId)
        }

        val likeCount = reviewService.getLikeCount(reviewId)
        ReviewLikeApi.LikeResponse(
            liked = false,
            likeCount = likeCount
        )
    }

    private suspend fun validateLikeAction(
        passport: Passport,
        reviewId: Long
    ) {
        // 회원 검증
        memberValidService.validateMember(passport)
        // 리뷰 검증, 본인이 작성한 리뷰도 도움돼요 가능
        reviewService.getReview(reviewId)
    }

}