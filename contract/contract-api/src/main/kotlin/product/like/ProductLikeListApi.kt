package product.like

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport
import product.product.ProductDto

interface ProductLikeListApi {
    companion object {
        const val PATH = "/api/product/like"
    }

    @Documented(
        summary = "좋아요를 누른 상품 목록 API",
        description = "최근에 좋아요를 누른 기준으로 정렬된 상품 목록 API",
        response = Response::class,
    )
    suspend fun list(passport: Passport): ApiResponse<Response>

    data class Response(
        @Schema(description = "해당 회원에 대한 좋아요 여부", example = "true")
        val productList: List<ProductDto>
    )
}