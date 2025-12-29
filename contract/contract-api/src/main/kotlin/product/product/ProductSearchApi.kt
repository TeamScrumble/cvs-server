package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

interface ProductSearchApi {
    companion object {
        const val PATH = "/api/product/search"
    }

    @Documented(
        summary = "상품 검색 API",
        description = "상품을 검색하는 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun search(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "편의점 종류", example = "GS25")
        @field:NotBlank
        val cvsTarget: String,

        @Schema(description = "상품명", example = "불닭마요 삼각김밥")
        @field:NotBlank
        @field:Size(min = 2)
        val title: String,

        @Schema(description = "현재 페이지", example = "1")
        @field:Min(0)
        val page: Int,

        @Schema(description = "페이지 당 상품 수(rpp)", example = "20")
        @field:Min(0)
        @field:Max(50)
        val size: Int,
    )

    data class Response(
        @Schema(description = "상품 정보")
        val product: List<ProductDto>
    )
}