package product.product.presentation.rest

import ApiResponse
import cvs.crawler.CvsTarget
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.product.*
import product.product.application.service.ProductLikeService
import product.product.application.service.ProductService
import product.product.application.utils.toCrawlerResultDto
import product.product.application.utils.toResponse
import security.passport.RequestPassport

@RestController
class ProductController(
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
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
        val saveCount = productService.saveAll(passport, request.toCrawlerResultDto()).size.toLong()
        val response = ProductAddApi.Response(saveCount)

        return ApiResponse.Success(response)
    }

    @GetMapping("${ProductGetApi.PATH}/{id}")
    override suspend fun get(
        @RequestPassport passport: Passport?,
        @PathVariable id: Long
    ): ApiResponse<ProductGetApi.Response> {
        val isLiked = if (passport != null) {
            productLikeService.toggle(id, passport.memberId).liked
        } else false

        val product = productService.findById(id).toResponse()

        return ApiResponse.Success(ProductGetApi.Response(product, isLiked))
    }

    @GetMapping(ProductListApi.PATH)
    override suspend fun list(
        @RequestBody request: ProductListApi.Request
    ): ApiResponse<ProductListApi.Response> {
        val product = productService.findAllByCvsTarget(CvsTarget.valueOf(request.cvsTarget)).map {
            it.toResponse()
        }

        return ApiResponse.Success(ProductListApi.Response(product))
    }

    @GetMapping(ProductSearchApi.PATH)
    override suspend fun search(
        @RequestBody @Valid request: ProductSearchApi.Request
    ): ApiResponse<ProductSearchApi.Response> {
        val pageable = PageRequest.of(request.page.coerceAtLeast(0), request.size.coerceIn(1, 100))

        return ApiResponse.Success(ProductSearchApi.Response(
            productService.findAllByKeyword(CvsTarget.valueOf(request.cvsTarget), request.title, pageable)
        ))
    }
}