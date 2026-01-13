package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.GetMapping
import passport.Passport

interface ReviewGetApi {

    @Documented(
        summary = "상품 리뷰 조회 API",
        description = "상품의 리뷰를 조회하는 API",
        response = ReviewDto::class
    )
    suspend fun get(
        passport: Passport,
        @Parameter(description = "상품 리뷰 id", example = "1", `in` = ParameterIn.PATH)
        reviewId: Long
    ): ApiResponse<ReviewDto>


}