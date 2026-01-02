package product.product.elasticsearch.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import product.product.domain.repository.ProductRepository
import product.product.elasticsearch.repository.ProductEsRepository
import product.product.elasticsearch.util.toDocument

@Service
class ProductEsSyncService(
    private val productRepository: ProductRepository,
    private val productEsRepository: ProductEsRepository
) {
    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { block() }

    suspend fun upsertByProductIds(productIds: List<Long>) {
        if (productIds.isEmpty()) return

        // 중복 제거 + 유효값만
        val uniqueIds = productIds.asSequence()
            .filter { it > 0 }
            .distinct()
            .toList()

        if (uniqueIds.isEmpty()) return

        val chunkSize = 1000

        uniqueIds.chunked(chunkSize).forEach { chunk ->
            val products = productRepository.findAllById(chunk).toList()
            if (products.isEmpty()) return@forEach

            val docs = products.map { it.toDocument() }
            io { productEsRepository.saveAll(docs) }
        }
    }

    suspend fun initialLoad(pageSize: Int = 2000) {
        var offset = 0L

        while (true) {
            val products = productRepository.findPageByOffset(offset, pageSize).toList()
            if (products.isEmpty()) break

            val docs = products.map { it.toDocument() }
            productEsRepository.saveAll(docs)

            offset += pageSize
        }
    }
}