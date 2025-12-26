package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ProductGetApi {
    companion object {
        const val PATH = "/api/product"
    }

    @Documented(
        summary = "상품 조회 API",
        description = "상품의 상세 정보를 조회하는 API",
        response = Response::class,
    )
    suspend fun get(passport: Passport?, id: Long): ApiResponse<Response>

    data class Response(
        @Schema(description = "상품 정보")
        val product: ProductDto,

        @Schema(description = "좋아요 여부", example = "true")
        val isLiked: Boolean
    )
}