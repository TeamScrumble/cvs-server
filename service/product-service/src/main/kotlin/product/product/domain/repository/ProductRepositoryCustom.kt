package product.product.domain.repository

import cvs.crawler.CvsTarget
import kotlinx.coroutines.flow.Flow
import product.product.domain.table.Product

interface ProductRepositoryCustom {
    suspend fun upsertAll(products: List<Product>, crawlRunId: String): List<Long>

    suspend fun getLikeCount(productId: Long): Int

    suspend fun incrementLikeCount(productId: Long): Long
    suspend fun decrementLikeCount(productId: Long): Long

    fun findIdsToSoftDelete(target: CvsTarget, crawlRunId: String): Flow<Long>
    suspend fun softDeleteByIds(ids: List<Long>): Long
}