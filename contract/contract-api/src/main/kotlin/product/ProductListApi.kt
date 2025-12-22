package product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ProductListApi {
    companion object {
        const val PATH = "/api/product"
    }

    @Documented(
        summary = "상품 목록 조회 API",
        description = "상품의 목록 정보를 조회하는 API",
        response = ProductGetApi.Response::class,
    )
    suspend fun list(request: Request): ApiResponse<Response>

    data class Request(
        val cvsTarget: String
    )

    data class Response(
        @Schema(description = "상품 정보")
        val product: List<ProductBaseResponse>
    )
}