package product.review.presentation

import ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.review.application.ReviewService
import review.ReviewAddApi
import review.ReviewApi
import security.passport.RequestPassport

@RestController
class ReviewController(
    private val reviewService: ReviewService
) : ReviewApi {

    @PostMapping(ReviewAddApi.PATH)
    override suspend fun add(
//        @RequestPassport passport: Passport,
        @RequestBody request: ReviewAddApi.Request
    ): ApiResponse<ReviewAddApi.Response> {
//        val reviewId = reviewService.add(passport, request)
        val reviewId = reviewService.add(request)
        val response = ReviewAddApi.Response(reviewId)

        return ApiResponse.Success(response)
    }

}