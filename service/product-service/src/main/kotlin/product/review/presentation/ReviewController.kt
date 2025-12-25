package product.review.presentation

import ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.review.application.ReviewFacade
import review.*

@RestController
@RequestMapping(ReviewApi.PATH)
class ReviewController(
    private val reviewFacade: ReviewFacade
) : ReviewApi {

    @PostMapping
    override suspend fun add(
//        @RequestPassport passport: Passport,
        @RequestBody @Valid request: ReviewAddApi.Request
    ): ApiResponse<ReviewAddApi.Response> {
//        val reviewId = reviewService.add(passport, request)
        val reviewId = reviewFacade.add(request)
        val response = ReviewAddApi.Response(reviewId)

        return ApiResponse.Success(response)
    }

    @GetMapping("/{reviewId}")
    override suspend fun get(
        @PathVariable reviewId: Long
    ): ApiResponse<ReviewGetApi.Response> {
        TODO("Not yet implemented")
    }

    @GetMapping
    override suspend fun list(
        @Valid @ModelAttribute request: ReviewListApi.Request
    ): ApiResponse<List<ReviewGetApi.Response>> {
        // todo 로그인 회원 꺼내기
        val memberId = 1L
        val result = reviewFacade.getReviewList(
            memberId = memberId,
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