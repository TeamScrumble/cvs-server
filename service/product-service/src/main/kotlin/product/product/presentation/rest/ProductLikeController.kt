package product.product.presentation.rest

import ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.like.ProductLikeApi
import product.like.ProductLikeListApi
import product.like.ProductLikeToggleApi
import product.product.application.service.ProductLikeFacade
import security.passport.RequestPassport

@RestController
class ProductLikeController(
    private val productLikeFacade: ProductLikeFacade
) : ProductLikeApi {
    @PostMapping(ProductLikeToggleApi.PATH)
    override suspend fun toggle(
        @RequestPassport passport: Passport,
        @RequestBody request: ProductLikeToggleApi.Request
    ): ApiResponse<ProductLikeToggleApi.Response> {
        val (isLiked, likeCount) = productLikeFacade.toggle(passport, request.productId)

        return ApiResponse.Success(ProductLikeToggleApi.Response(isLiked, likeCount))
    }

    @GetMapping(ProductLikeListApi.PATH)
    override suspend fun list(
        @RequestPassport passport: Passport,
    ): ApiResponse<ProductLikeListApi.Response> {
        val productList = productLikeFacade.list(passport)

        return ApiResponse.Success(ProductLikeListApi.Response(productList))
    }
}