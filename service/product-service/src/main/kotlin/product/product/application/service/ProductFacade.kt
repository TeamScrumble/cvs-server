package product.product.application.service

import cvs.crawler.CvsTarget
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService
import product.profanity.valid.ProfanityFilterService
import product.product.ProductDto
import product.product.application.utils.toResponse

@Service
class ProductFacade(
    private val memberValidService: MemberValidService,
    private val productLikeService: ProductLikeService,
    private val profanityFilterService: ProfanityFilterService,
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

    suspend fun findAllByKeyword(
        cvsTarget: CvsTarget?,
        keyword: String,
        pageable: Pageable
    ): List<ProductDto> {
        val result = profanityFilterService.check(keyword)
        if (result.hasBadWord) {
            throw BusinessException(ProductErrorCode.P_011)
        }

        return productService.findAllByKeyword(cvsTarget, keyword, pageable)
    }
}