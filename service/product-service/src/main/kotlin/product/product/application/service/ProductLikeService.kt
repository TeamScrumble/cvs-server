package product.product.application.service

import db.transactional.Transactional
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import product.product.domain.repository.ProductLikeRepository
import product.product.domain.repository.ProductRepository
import product.product.domain.table.ProductLike

@Service
class ProductLikeService(
    private val transactional: Transactional,
    private val productLikeRepository: ProductLikeRepository,
    private val productRepository: ProductRepository
) {
    suspend fun list(memberId: Long) =
        productLikeRepository.findLikedProductsByMemberId(memberId)

    suspend fun toggle(productId: Long, memberId: Long): ToggleResult = transactional {
        // 1) 먼저 삭제 시도
        val deleted = productLikeRepository.deleteByProductIdAndMemberId(productId, memberId)

        // 1-1) 삭제된 row가 존재할 경우
        if (deleted > 0) {
            productRepository.decrementLikeCount(productId)
            val likeCount = productRepository.getLikeCount(productId)

            return@transactional ToggleResult(liked = false, likeCount = likeCount)
        }

        // 2) 삭제된 row가 없을 경우 → insert 시도
        val inserted = try {
            productLikeRepository.save(
                ProductLike(
                    productId = productId,
                    memberId = memberId
                )
            )
            true
        } catch (e: DuplicateKeyException) {
            false
        }

        // insert가 실제로 성공했을 때만 count 증가 (중복이면 증가 금지)
        if (inserted) {
            productRepository.incrementLikeCount(productId)
        }

        val likeCount = productRepository.getLikeCount(productId)
        ToggleResult(liked = true, likeCount = likeCount)
    }

    data class ToggleResult(
        val liked: Boolean,
        val likeCount: Int
    )
}