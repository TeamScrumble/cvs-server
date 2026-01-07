package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ReviewDeleteApi {

    @Documented(
        summary = "상품 리뷰 삭제 API",
        description = "상품 리뷰를 삭제하는 API",
        response = Response::class
    )
    suspend fun delete(
        passport: Passport,
        @Parameter(
            description = "삭제할 리뷰 id",
            example = "1",
            `in` = ParameterIn.PATH
        ) reviewId: Long
    ): ApiResponse<Response>

    data class Response(
        @Schema(description = "삭제 처리된 리뷰 id", example = "1")
        val reviewId: Long
    )

}