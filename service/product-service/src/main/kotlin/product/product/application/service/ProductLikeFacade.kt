package product.product.application.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService
import product.product.ProductDto
import product.product.application.utils.toResponse

@Service
class ProductLikeFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val productService: ProductService
) {
    suspend fun list(passport: Passport): List<ProductDto> {
        memberValidService.validateMember(passport)

        return productLikeService.list(passport.memberId)
            .map { it.toResponse() }
            .toList()
    }

    suspend fun toggle(passport: Passport, productId: Long): ProductLikeService.ToggleResult {
        memberValidService.validateMember(passport)
        productService.validateExists(productId)

        val memberId = passport.memberId

        // DB에서 원자적으로 토글 + 최신 likeCount 확보
        val result = productLikeService.toggle(productId, memberId)

        return result
    }
}