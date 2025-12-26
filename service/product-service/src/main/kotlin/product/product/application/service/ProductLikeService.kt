package product.product.application.service

import org.springframework.stereotype.Service
import product.product.domain.repository.ProductCustomRepository
import product.product.domain.table.ProductLike
import product.product.domain.repository.ProductLikeRepository

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