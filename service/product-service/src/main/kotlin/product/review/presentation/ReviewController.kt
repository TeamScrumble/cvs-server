package product.review.presentation

import ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.review.application.ReviewFacade
import review.*
import security.passport.RequestPassport

@RestController
@RequestMapping(ReviewApi.PATH)
class ReviewController(
    private val reviewFacade: ReviewFacade
) : ReviewApi {

    @PostMapping
    override suspend fun add(
        @RequestPassport passport: Passport,
        @RequestBody @Valid request: ReviewAddApi.Request
    ): ApiResponse<ReviewAddApi.Response> {
        val reviewId = reviewFacade.add(passport, request)
        val response = ReviewAddApi.Response(reviewId)

        return ApiResponse.Success(response)
    }

    @GetMapping("/{reviewId}")
    override suspend fun get(
        @RequestPassport passport: Passport,
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewGetApi.Response> {
        val result = reviewFacade.getReview(
            passport = passport,
            reviewId = reviewId
        )

        return ApiResponse.Success(result)
    }

    @GetMapping
    override suspend fun list(
        @RequestPassport passport: Passport,
        @Valid @ModelAttribute request: ReviewListApi.Request
    ): ApiResponse<List<ReviewGetApi.Response>> {

        val result = reviewFacade.getReviewList(
            passport = passport,
            request = request
        )

        return ApiResponse.Success(result)
    }

    @GetMapping("/summary")
    override suspend fun getSummary(
        @RequestParam productId: Long
    ): ApiResponse<ReviewSummaryGetApi.Response> {
        val result = reviewFacade.getSummary(productId)

        return ApiResponse.Success(result)
    }

    @GetMapping("/aspectInfo")
    override suspend fun getAspectInfo():
            ApiResponse<List<ReviewAspectGetApi.Response>> {
        val result = reviewFacade.getAspectInfo()

        return ApiResponse.Success(result)
    }


}