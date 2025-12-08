package product.presentation

import ApiResponse
import member.MemberApiClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.ProductAddApi
import product.ProductApi
import product.product.application.ProductService
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
}