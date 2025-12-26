package product.product.application.service

import db.transactional.Transactional
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import passport.Passport
import product.product.ProductDto
import product.common.valid.MemberValidService
import product.product.application.utils.toResponse

@Service
class ProductLikeFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val productService: ProductService,
    private val transactional: Transactional
) {
    suspend fun list(
        passport: Passport
    ): List<ProductDto> {
        memberValidService.validateMember(passport)

        return productLikeService.list(passport.memberId).map { it.toResponse() }.toList()
    }

    suspend fun toggle(
        passport: Passport,
        productId: Long,
    ): ToggleResult {
        memberValidService.validateMember(passport)
        productService.validateExists(productId)

        val memberId = passport.memberId

        return transactional {
            if (productLikeService.isLiked(productId, memberId)) {
                productLikeService.unlike(productId, memberId)
            } else {
                productLikeService.like(productId, memberId)
            }
        }
    }

    data class ToggleResult(
        val liked: Boolean,
        val likeCount: Int
    )
}