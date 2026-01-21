package product.review.presentation

import ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.review.application.ReviewFacade
import review.*
import security.passport.RequestPassport

@RestController
class ReviewController(
    private val reviewFacade: ReviewFacade
) : ReviewApi {

    @PostMapping(ReviewPaths.PRODUCT_BASE)
    override suspend fun add(
        @RequestPassport passport: Passport,
        @RequestBody @Valid request: ReviewAddApi.Request,
        @PathVariable productId: Long
    ): ApiResponse<ReviewAddApi.Response> {
        val reviewId = reviewFacade.add(passport, request, productId)
        val response = ReviewAddApi.Response(reviewId)

        return ApiResponse.Success(response)
    }

    @GetMapping(ReviewPaths.REVIEW)
    override suspend fun get(
        @RequestPassport passport: Passport,
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewDto> {
        val result = reviewFacade.getReview(
            passport = passport,
            reviewId = reviewId
        )

        return ApiResponse.Success(result)
    }

    @GetMapping(ReviewPaths.PRODUCT_BASE)
    override suspend fun list(
        @RequestPassport passport: Passport,
        @Valid @ModelAttribute request: ReviewListApi.Request,
        @PathVariable productId: Long
    ): ApiResponse<ReviewListApi.Response> {

        val result = reviewFacade.getReviewList(
            passport = passport,
            request = request,
            productId = productId
        )

        return ApiResponse.Success(result)
    }

    @DeleteMapping(ReviewPaths.REVIEW)
    override suspend fun delete(
        @RequestPassport passport: Passport,
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewDeleteApi.Response> {
        val deleted = reviewFacade.delete(
            passport = passport,
            reviewId = reviewId
        )
        val result = ReviewDeleteApi.Response(deleted)

        return ApiResponse.Success(result)
    }

    @GetMapping(ReviewPaths.REVIEW_SUMMARY)
    override suspend fun getSummary(
        @RequestPassport passport: Passport?,
        @PathVariable productId: Long
    ): ApiResponse<ReviewSummaryGetApi.Response> {
        val result = reviewFacade.getSummaryUnified(passport, productId)

        return ApiResponse.Success(result)
    }

    @GetMapping(ReviewPaths.ASPECT_INFO)
    override suspend fun getAspectInfo():
            ApiResponse<List<ReviewAspectGetApi.Response>> {
        val result = reviewFacade.getAspectInfo()

        return ApiResponse.Success(result)
    }


}