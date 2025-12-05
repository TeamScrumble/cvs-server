package product.presentation

import ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import product.ProductAddApi
import product.ProductApi
import product.product.application.ProductService
import product.util.toCrawlerResultDto

@RestController
class ProductController(
    private val productService: ProductService
) : ProductApi {

    @PostMapping(ProductAddApi.PATH)
    override suspend fun add(@RequestBody request: List<ProductAddApi.Request>): ApiResponse<ProductAddApi.Response> {
        val saveCount = productService.saveAll(request.toCrawlerResultDto())
        val response = ProductAddApi.Response(saveCount)

        return ApiResponse.Success(response)
    }
}