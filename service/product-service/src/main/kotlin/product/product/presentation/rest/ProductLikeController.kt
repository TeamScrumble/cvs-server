package product.product.presentation.rest

import ApiResponse
import cvs.crawler.CvsTarget
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.ProductApi
import product.ProductLikeApi
import product.product.application.ProductLikeFacade
import product.product.application.ProductLikeService
import product.product.application.ProductService
import security.passport.RequestPassport

@RestController
class ProductLikeController(
    private val productLikeFacade: ProductLikeFacade
) : ProductLikeApi {
    @PostMapping(ProductLikeApi.PATH)
    override suspend fun toggle(
        @RequestPassport passport: Passport,
        @RequestBody request: ProductLikeApi.Request
    ): ApiResponse<ProductLikeApi.Response> {
        val (isLiked, likeCount) = productLikeFacade.toggle(passport, request.productId)

        return ApiResponse.Success(ProductLikeApi.Response(isLiked, likeCount))
    }
}