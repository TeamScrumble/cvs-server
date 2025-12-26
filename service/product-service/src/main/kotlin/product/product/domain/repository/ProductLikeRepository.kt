package product.product.domain.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.product.domain.table.Product
import product.product.domain.table.ProductLike

@Repository
interface ProductLikeRepository : CoroutineCrudRepository<ProductLike, Long> {
    @Query(
        """
        SELECT p.*
        FROM product_like pl
        JOIN product p ON p.product_id = pl.product_id
        WHERE pl.member_id = :memberId
        ORDER BY pl.created_at DESC
        """
    )
    fun findLikedProductsByMemberId(
        memberId: Long
    ): Flow<Product>

    suspend fun existsByProductIdAndMemberId(
        productId: Long,
        memberId: Long
    ): Boolean

    suspend fun deleteByProductIdAndMemberId(
        productId: Long,
        memberId: Long
    )
}