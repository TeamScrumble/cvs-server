package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

interface ReviewListApi {

    @Documented(
        summary = "상품 리뷰 목록 조회 API",
        description = "상품의 리뷰 목록을 조회하는 API",
        response = ReviewGetApi.Response::class
    )
    suspend fun list(
        @RequestParam
        @Parameter(
            description = "상품 id", example = "1", `in` = ParameterIn.QUERY)
        productId: Long,

        @RequestParam(defaultValue = "0")
        @Parameter(description = "페이지 번호(0부터 시작)", example = "1", `in` = ParameterIn.QUERY)
        page: Int = 0,

        @RequestParam(name = "size", defaultValue = "10")
        @Parameter(description = "페이지 크기", example = "10", `in` = ParameterIn.QUERY)
        pageSize: Int = 10,

        @RequestParam(defaultValue = "recommended")
        @Parameter(
            description = "정렬 기준",
            example = "recommended/latest/rating_high/rating_low/most_helpful",
            `in` = ParameterIn.QUERY
        )
        sort: String = "recommended"
    ): ApiResponse<List<ReviewGetApi.Response>>

}