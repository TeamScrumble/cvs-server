package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import passport.Passport
import product.review.domain.ReviewSortType
import product.review.domain.entity.Review
import product.review.domain.repository.ReviewRepository
import review.ReviewAddApi
import review.ReviewGetApi

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewScoreService: ReviewScoreService
) {
    @Transactional
    suspend fun add(
        //passport: Passport,
        request: ReviewAddApi.Request
    ): Long {

        // 회원 검증
        //validateMember(passport)
        // 리뷰 검증
        validateReview(request)

        val memberId = 1L // TODO: passport.memberId로 변경
        val review = Review(
            productId = request.productId,
            memberId = memberId,
            rating = request.rating,
            content = request.content,
            isReceipt = request.isReceipt
        )

        // 리뷰 저장
        val saved = reviewRepository.save(review)

        // 리뷰 평가 점수 저장
        reviewScoreService.addScores(saved.id, request.scores)

        return saved.id
    }

    @Transactional(readOnly = true)
    suspend fun getList(
        productId: Long,
        page: Int,
        size: Int,
        sortStr: String
    ): List<ReviewGetApi.Response> {
        val sortType = ReviewSortType.from(sortStr)
        val sort = getSort(sortType)
        val pageable = PageRequest.of(page, size, sort)
        val reviews = reviewRepository
            .findByProductIdAndIsDeletedFalse(
                productId,
                pageable
            ).collectList()

        return emptyList()
    }

    private fun getSort(sortType: ReviewSortType): Sort {
        return when(sortType) {
            ReviewSortType.LATEST -> Sort.by(Sort.Order.desc("createdAt"))
            ReviewSortType.RATING_HIGH -> Sort.by(
                Sort.Order.desc("rating"),
                Sort.Order.desc("createdAt")
            )
            ReviewSortType.RATING_LOW -> Sort.by(
                Sort.Order.asc("rating"),
                Sort.Order.desc("createdAt")
            )
            else -> Sort.unsorted()
        }
    }

    private suspend fun validateMember(passport: Passport) {
        // 실제 존재하는 회원인지
        // 권한 체크
    }

    private suspend fun validateReview(request: ReviewAddApi.Request) {
        // TODO: 상품 검증(상품 존재 여부)

        // 리뷰 검증
        // 만족도 범위 체크
        if (request.rating !in RATING_MIN..RATING_MAX) {
            throw BusinessException(ReviewErrorCode.R_002)
        }
        // 리뷰 글자수 체크
        val contentLength = request.content.trim().length
        if (contentLength !in CONTENT_MIN..CONTENT_MAX) {
            throw BusinessException(ReviewErrorCode.R_003)
        }
    }

    companion object {
        private const val RATING_MIN = 1
        private const val RATING_MAX = 5
        private const val CONTENT_MIN = 10
        private const val CONTENT_MAX = 500
    }

}