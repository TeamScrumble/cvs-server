package product.product.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductLikeRepository : CoroutineCrudRepository<ProductLike, Long> {

    suspend fun existsByProductIdAndMemberId(
        productId: Long,
        memberId: Long
    ): Boolean

    suspend fun deleteByProductIdAndMemberId(
        productId: Long,
        memberId: Long
    )
}