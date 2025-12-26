package product.product.application

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import db.transactional.Transactional
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import member.MemberApi
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import passport.Passport
import passport.isAdmin
import product.product.domain.ProductCustomRepository
import product.product.domain.ProductLike
import product.product.domain.ProductLikeRepository
import product.product.domain.ProductRepository

@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val productCustomRepository: ProductCustomRepository
) {
    suspend fun list(memberId: Long) = productLikeRepository.findLikedProductsByMemberId(memberId)

    suspend fun isLiked(productId: Long, memberId: Long): Boolean =
        productLikeRepository.existsByProductIdAndMemberId(productId, memberId)

    suspend fun like(productId: Long, memberId: Long): ProductLikeFacade.ToggleResult {
        productLikeRepository.save(
            ProductLike(
                productId = productId,
                memberId = memberId
            )
        )
        productCustomRepository.incrementLikeCount(productId)

        return ProductLikeFacade.ToggleResult(
            liked = true,
            likeCount = productCustomRepository.getLikeCount(productId)
        )
    }

    suspend fun unlike(productId: Long, memberId: Long): ProductLikeFacade.ToggleResult {
        productLikeRepository.deleteByProductIdAndMemberId(productId, memberId)
        productCustomRepository.decrementLikeCount(productId)

        return ProductLikeFacade.ToggleResult(
            liked = false,
            likeCount = productCustomRepository.getLikeCount(productId)
        )
    }
}