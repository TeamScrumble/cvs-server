package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
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
        @ModelAttribute request: Request
    ): ApiResponse<List<ReviewGetApi.Response>>

    data class Request(
        @RequestParam
        @Parameter(
            description = "상품 id", example = "1", `in` = ParameterIn.QUERY)
        val productId: Long,

        @RequestParam(defaultValue = "0")
        @Parameter(description = "페이지 번호(0부터 시작)", example = "1", `in` = ParameterIn.QUERY)
        val page: Int = 0,

        @RequestParam(name = "size", defaultValue = "10")
        @Parameter(description = "페이지 크기", example = "10", `in` = ParameterIn.QUERY)
        val pageSize: Int = 10,

        @RequestParam(defaultValue = "false")
        @Parameter(description = "영수증 인증 리뷰만 보기", example = "false", `in` = ParameterIn.QUERY)
        val receiptOnly: Boolean = false,

        @RequestParam(defaultValue = "false")
        @Parameter(description = "이미지 리뷰만 보기", example = "false", `in` = ParameterIn.QUERY)
        val imageOnly: Boolean = false,

        @RequestParam(defaultValue = "RECOMMENDED")
        @Parameter(
            description = "정렬 기준",
            example = "RECOMMENDED/LATEST/RATING_HIGH/RATING_LOW/MOST_HELPFUL",
            `in` = ParameterIn.QUERY
        )
        val sort: ReviewSortType = ReviewSortType.RECOMMENDED
    )

    data class Response(
        val reviews: List<ReviewGetApi.Response>
    )
}