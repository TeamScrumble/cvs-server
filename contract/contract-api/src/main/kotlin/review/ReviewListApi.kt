package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import org.springdoc.core.annotations.ParameterObject
import passport.Passport

interface ReviewListApi {

    @Documented(
        summary = "상품 리뷰 목록 조회 API",
        description = "상품의 리뷰 목록을 조회하는 API",
        request = Request::class,
        response = Response::class

    )
    suspend fun list(
        passport: Passport,
        @ParameterObject
        request: Request,
        @Parameter(description = "상품 id", example = "1", `in` = ParameterIn.PATH)
        productId: Long
    ): ApiResponse<Response>

    data class Request(
        @field:Parameter(description = "페이지 번호(0부터 시작)", example = "1", `in` = ParameterIn.QUERY)
        val page: Int = 0,

        @field:Parameter(description = "페이지 크기", example = "10", `in` = ParameterIn.QUERY)
        val pageSize: Int = 10,

        @field:Parameter(description = "영수증 인증 리뷰만 보기", example = "false", `in` = ParameterIn.QUERY)
        val receiptOnly: Boolean = false,

        @field:Parameter(description = "이미지 리뷰만 보기", example = "false", `in` = ParameterIn.QUERY)
        val imageOnly: Boolean = false,

        @field:Parameter(
            description = "정렬 기준",
            example = "RECOMMENDED",
            `in` = ParameterIn.QUERY
        )
        val sort: ReviewSortType = ReviewSortType.RECOMMENDED
    )

    data class Response(
        val reviews: List<ReviewDto>
    )

}