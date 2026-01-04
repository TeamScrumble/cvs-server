package review.like

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import passport.Passport

interface ReviewLikeAddApi {

    @Documented(
        summary = "상품 리뷰 도움돼요 추가 API",
        description = "상품 리뷰에 도움돼요를 추가하는 API",
        request = Unit::class,
        response = ReviewLikeApi.LikeResponse::class
    )
    suspend fun likeAdd(
        passport: Passport,
        @Parameter(
            description = "도움돼요 대상 리뷰 id",
            example = "1",
            `in` = ParameterIn.PATH
        ) reviewId: Long
    ): ApiResponse<ReviewLikeApi.LikeResponse>

}