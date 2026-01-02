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
    suspend fun search(@ParameterObject request: Request): ApiResponse<Response>

    data class Request(
        @Parameter(description = "편의점 종류", example = "ALL/GS25/SEVEN_ELEVEN/CU/EMART_24", `in` = ParameterIn.QUERY)
        val cvsTarget: String,

        @Parameter(description = "페이지", example = "0", `in` = ParameterIn.QUERY)
        val page: Int,

        @Parameter(description = "상품명", example = "불닭", `in` = ParameterIn.QUERY)
        val keyword: String,
    )

    data class Response(
        @Schema(description = "상품 정보")
        val product: List<ProductDto>
    )
}