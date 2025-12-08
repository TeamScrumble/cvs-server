package product.presentation

import ApiResponse
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.ProductAddApi
import product.ProductApi
import product.ProductGetApi
import product.product.application.ProductService
import product.product.domain.Product
import product.util.toCrawlerResultDto
import security.passport.RequestPassport

@RestController
class ProductController(
    private val productService: ProductService
) : ProductApi {
    @PostMapping(ProductAddApi.PATH)
    override suspend fun add(
        @RequestPassport passport: Passport,
        @RequestBody request: List<ProductAddApi.Request>
    ): ApiResponse<ProductAddApi.Response> {
        val saveCount = productService.saveAll(passport, request.toCrawlerResultDto())
        val response = ProductAddApi.Response(saveCount)

        return ApiResponse.Success(response)
    }

    @GetMapping("${ProductGetApi.PATH}/{id}")
    override suspend fun get(
        @RequestPassport passport: Passport,
        @PathVariable id: Long
    ): ApiResponse<ProductGetApi.Response> {
        val product = productService.findById(passport, id).toResponse()

        return ApiResponse.Success(product)
    }

    private fun Product.toResponse() = ProductGetApi.Response(
        id, cvsTarget.name, title, img, price, event, isNewProduct
    )
}