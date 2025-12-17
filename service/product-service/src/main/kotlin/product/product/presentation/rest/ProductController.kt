package product.product.presentation.rest

import ApiResponse
import cvs.crawler.CvsTarget
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.ProductAddApi
import product.ProductApi
import product.ProductCrawlApi
import product.ProductGetApi
import product.ProductListApi
import product.product.application.ProductService
import product.product.domain.Product
import product.product.application.toCrawlerResultDto
import security.passport.RequestPassport

@RestController
class ProductController(
    private val productService: ProductService
) : ProductApi {
    @PostMapping(ProductCrawlApi.PATH)
    override suspend fun crawl(
        @RequestPassport passport: Passport,
        @RequestBody request: List<ProductCrawlApi.Request>
    ): ApiResponse<ProductCrawlApi.Response> {
        val isSuccess = productService.crawl(passport, request.map { CvsTarget.valueOf(it.cvsTarget) })

        return ApiResponse.Success(ProductCrawlApi.Response(isSuccess))
    }

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
        @PathVariable id: Long
    ): ApiResponse<ProductGetApi.Response> {
        val product = productService.findById(id).toResponse()

        return ApiResponse.Success(product)
    }

    @GetMapping(ProductListApi.PATH)
    override suspend fun list(
        @RequestBody request: ProductListApi.Request
    ): ApiResponse<List<ProductGetApi.Response>> {
        val product = productService.findAllByCvsTarget(CvsTarget.valueOf(request.cvsTarget)).map {
            it.toResponse()
        }

        return ApiResponse.Success(product)
    }

    private fun Product.toResponse() = ProductGetApi.Response(
        id, cvsTarget.name, title, img, price, event, isNewProduct
    )
}