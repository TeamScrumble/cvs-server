package product.product.application

import db.transactional.Transactional
import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService

@Service
class ProductLikeFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val productService: ProductService,
    private val transactional: Transactional
) {

    suspend fun toggle(
        passport: Passport,
        productId: Long,
    ): ToggleResult = transactional {
        memberValidService.validateMember(passport)
        productService.validateExists(productId)

        val memberId = passport.memberId

        if (productLikeService.isLiked(productId, memberId)) {
            productLikeService.unlike(productId, memberId)
        } else {
            productLikeService.like(productId, memberId)
        }
    }

    data class ToggleResult(
        val liked: Boolean,
        val likeCount: Int
    )
}