package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import passport.Passport
import product.review.domain.entity.Review
import product.review.domain.repository.ReviewRepository
import review.ReviewAddApi

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
            content = request.content
        )

        // 리뷰 저장
        val saved = reviewRepository.save(review)

        // 리뷰 평가 점수 저장
        reviewScoreService.addScores(saved.id, request.scores)

        return saved.id
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