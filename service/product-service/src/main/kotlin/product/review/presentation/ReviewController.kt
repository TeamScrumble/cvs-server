package product.review.presentation

import ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.review.application.ReviewService
import review.*
import security.passport.RequestPassport

@RestController
@RequestMapping(ReviewApi.PATH)
class ReviewController(
    private val reviewService: ReviewService
) : ReviewApi {

    override suspend fun add(
//        @RequestPassport passport: Passport,
        @RequestBody request: ReviewAddApi.Request
    ): ApiResponse<ReviewAddApi.Response> {
//        val reviewId = reviewService.add(passport, request)
        val reviewId = reviewService.add(request)
        val response = ReviewAddApi.Response(reviewId)

        return ApiResponse.Success(response)
    }

    override suspend fun get(
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewGetApi.Response> {
        TODO("Not yet implemented")
    }

    override suspend fun list(
        @RequestParam productId: Long,
        @RequestParam page: Int,
        @RequestParam pageSize: Int,
        @RequestParam sort: String
    ): ApiResponse<List<ReviewGetApi.Response>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSummary(
        @RequestParam productId: Long
    ): ApiResponse<ReviewSummaryGetApi.Response> {
        TODO("Not yet implemented")
    }


}