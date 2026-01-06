package product.product.domain.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import product.product.domain.table.Product

interface ProductRepository : CoroutineCrudRepository<Product, Long>, ProductRepositoryCustom {
    @Query(
        """
        SELECT *
        FROM product
        ORDER BY product_id
        LIMIT :limit OFFSET :offset
        """
    )
    fun findPageByOffset(
        offset: Long,
        limit: Int
    ): Flow<Product>
}
