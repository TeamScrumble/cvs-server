package product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ProductAddApi {
    companion object {
        const val PATH = "/api/product/internal"
    }

    @Documented(
        summary = "상품 생성 API",
        description = "크롤링된 상품 정보를 바탕으로 상품을 생성하는 내부 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun add(request: List<Request>): ApiResponse<Response>

    data class Request(
        @Schema(description = "편의점 종류", example = "GS25")
        val cvsTarget: String,

        @Schema(description = "상품명", example = "불닭마요 삼각김밥")
        val title: String,

        @Schema(description = "이미지 URL", example = "https://gs25.img/samgak.jpg")
        val img: String,

        @Schema(description = "가격", example = "1500")
        val price: Int,

        @Schema(description = "행사 정보", example = "1+1")
        val event: String,

        @Schema(description = "신상품 여부", example = "true")
        val isNew: Boolean
    )

    data class Response(
        @Schema(description = "등록된 상품의 개수", example = "10")
        val savedItemsCount: Int
    )
}