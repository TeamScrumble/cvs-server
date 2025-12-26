package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ProductCrawlApi {
    companion object {
        const val PATH = "/api/product/crawl/internal"
    }

    @Documented(
        summary = "상품 크롤링 등록 API",
        description = "크롤링된 상품을 등록하는 내부 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun crawl(passport: Passport, request: List<Request>): ApiResponse<Response>

    data class Request(
        @Schema(description = "편의점 종류", example = "CU, EMART_24, GS25, SEVEN_ELEVEN 중 1개")
        val cvsTarget: String
    )

    data class Response(
        @Schema(description = "요청 성공 여부 / 실패 시 에러 응답이 온다.", example = "true")
        val isSuccess: Boolean
    )
}