package product.product.presentation.rest

import ApiResponse
import cvs.crawler.CvsTarget
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.product.*
import product.product.application.service.ProductFacade
import product.product.application.service.ProductLikeService
import product.product.application.service.ProductService
import product.product.application.utils.toResponse
import security.passport.RequestPassport

@RestController
class ProductController(
    private val productFacade: ProductFacade,
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

    @PostMapping(ProductEsSyncApi.PATH)
    override suspend fun sync(
        @RequestPassport passport: Passport,
    ): ApiResponse<ProductEsSyncApi.Response> {
        val jobId = productService.sync(passport)

        return ApiResponse.Success(ProductEsSyncApi.Response(jobId))
    }

    @GetMapping("${ProductGetApi.PATH}/{id}")
    override suspend fun get(
        @RequestPassport passport: Passport?,
        @PathVariable id: Long
    ): ApiResponse<ProductGetApi.Response> {
        productFacade.findProduct(passport, id)
        val (product, isLiked) = productFacade.findProduct(passport, id)

        return ApiResponse.Success(ProductGetApi.Response(product, isLiked))
    }

    @GetMapping(ProductSearchApi.PATH)
    override suspend fun search(
        @ModelAttribute request: ProductSearchApi.Request
    ): ApiResponse<ProductSearchApi.Response> {
        val requestTarget = request.cvsTarget
        val (cvsTarget, keyword) = searchParamValidation(requestTarget, request.keyword)
        val rpp = 20

        val pageable = PageRequest.of(request.page.coerceAtLeast(0), rpp)

        return ApiResponse.Success(ProductSearchApi.Response(
            productService.findAllByKeyword(cvsTarget, keyword, pageable)
        ))
    }

    private fun searchParamValidation(requestTarget: String, keyword: String): Pair<CvsTarget?, String> {
        // ALL이 아닌 경우에만 Validation 체크
        val cvsTarget = if (requestTarget.uppercase() != "ALL") {
            CvsTarget(requestTarget) ?: throw BusinessException(ProductErrorCode.P_003)
        } else null

        if (keyword.length < 2) {
            throw BusinessException(ProductErrorCode.P_004)
        }

        return cvsTarget to keyword
    }
}