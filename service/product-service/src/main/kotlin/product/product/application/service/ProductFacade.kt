package product.product.application.service

import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService
import product.product.ProductDto
import product.product.application.utils.toResponse

@Service
class ProductFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val productService: ProductService
) {
    suspend fun findProduct(passport: Passport?, productId: Long): Pair<ProductDto, Boolean> {
        val isLiked = if (passport != null) {
            memberValidService.validateMember(passport)

            productLikeService.existByProductIdAndMemberId(productId, passport.memberId)
        } else false

        val product = productService.findById(productId)

        return product.toResponse() to isLiked
    }
}