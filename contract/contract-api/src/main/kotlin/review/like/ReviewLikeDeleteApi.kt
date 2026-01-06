package review.like

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import passport.Passport

interface ReviewLikeDeleteApi {

    @Documented(
        summary = "상품 리뷰 도움돼요 취소 API",
        description = "상품 리뷰에 도움돼요를 취소하는 API",
        request = Unit::class,
        response = ReviewLikeApi.LikeResponse::class
    )
    suspend fun likeRemove(
        passport: Passport,
        @Parameter(
            description = "도움돼요 취소 대상 리뷰 id",
            example = "1",
            `in` = ParameterIn.PATH
        ) reviewId: Long
    ): ApiResponse<ReviewLikeApi.LikeResponse>

}