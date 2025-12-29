package product.product.application.service

import db.transactional.Transactional
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import passport.Passport
import product.product.ProductDto
import product.common.valid.MemberValidService
import product.product.application.utils.toResponse
import product.product.elasticsearch.service.ProductEsService

@Service
class ProductLikeFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val productEsService: ProductEsService,
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

        // DB 확정 이후 ES 반영 (set 방식)
        productEsService.updateLikeCount(productId, result.likeCount)

        return result
    }
}