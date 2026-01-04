package product.review.presentation

import ApiResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.review.application.ReviewFacade
import review.like.ReviewLikeApi
import security.passport.RequestPassport

@RestController
@RequestMapping(ReviewLikeApi.PATH)
class ReviewLikeController(
    private val reviewFacade: ReviewFacade
) : ReviewLikeApi {

    @PostMapping
    override suspend fun likeAdd(
        @RequestPassport passport: Passport,
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewLikeApi.LikeResponse> {
        val result = reviewFacade.addReviewLike(
            passport = passport,
            reviewId = reviewId
        )

        return ApiResponse.Success(result)
    }

}