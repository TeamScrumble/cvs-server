package product.product.application.service

import db.transactional.Transactional
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import product.product.domain.repository.ProductCustomRepository
import product.product.domain.table.ProductLike
import product.product.domain.repository.ProductLikeRepository

@Service
class ProductLikeService(
    private val transactional: Transactional,
    private val productLikeRepository: ProductLikeRepository,
    private val productCustomRepository: ProductCustomRepository
) {
    suspend fun list(memberId: Long) =
        productLikeRepository.findLikedProductsByMemberId(memberId)

    suspend fun toggle(productId: Long, memberId: Long): ToggleResult = transactional {
        // 1) 먼저 삭제 시도
        val deleted = productLikeRepository.deleteByProductIdAndMemberId(productId, memberId)

        // 1-1) 삭제된 row가 존재할 경우
        if (deleted > 0) {
            productCustomRepository.decrementLikeCount(productId)
            val likeCount = productCustomRepository.getLikeCount(productId)

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
            productCustomRepository.incrementLikeCount(productId)
        }

        val likeCount = productCustomRepository.getLikeCount(productId)
        ToggleResult(liked = true, likeCount = likeCount)
    }

    data class ToggleResult(
        val liked: Boolean,
        val likeCount: Int
    )
}