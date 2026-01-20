package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springdoc.core.annotations.ParameterObject

interface ProductSearchRecommendApi {
    companion object {
        const val PATH = "/api/product/search-recommend"
    }

    @Documented(
        summary = "상품 검색 시 추천 상품을 보여주는 API",
        description = "상품을 검색할 때 검색한 키워드에 맞는 상품 정보를 보여주는 API",
        response = Response::class,
    )
    suspend fun searchRecommend(cvsTarget: String, keyword: String): ApiResponse<Response>

    data class Response(
        @Schema(description = "상품 정보")
        val product: List<ProductDocumentDto>
    )
}