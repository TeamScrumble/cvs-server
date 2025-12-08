package product.review.application

import extension.getOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import passport.Passport
import product.review.domain.entity.Review
import product.review.domain.repository.ReviewRepository
import review.ReviewAddApi

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
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

        val memberId = 1L
        val review = Review(
            productId = request.productId,
            memberId = memberId,
            rating = request.rating,
            content = request.content
        )

        val saved = reviewRepository.save(review)

        return saved.id
    }

    private suspend fun validateMember(passport: Passport) {
        // 실제 존재하는 회원인지
        // 권한 체크
    }

    private suspend fun validateReview(request: ReviewAddApi.Request) {
        // 상품 검증
        // 리뷰 검증
    }

}