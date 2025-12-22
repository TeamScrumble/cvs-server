package product.product.presentation.rest

import ApiResponse
import org.springframework.web.bind.annotation.*
import passport.Passport
import product.ProductLikeToggleApi
import product.product.application.ProductLikeFacade
import security.passport.RequestPassport

@RestController
class ProductLikeController(
    private val productLikeFacade: ProductLikeFacade
) : ProductLikeToggleApi {
    @PostMapping(ProductLikeToggleApi.PATH)
    override suspend fun toggle(
        @RequestPassport passport: Passport,
        @RequestBody request: ProductLikeToggleApi.Request
    ): ApiResponse<ProductLikeToggleApi.Response> {
        val (isLiked, likeCount) = productLikeFacade.toggle(passport, request.productId)

        return ApiResponse.Success(ProductLikeToggleApi.Response(isLiked, likeCount))
    }
}