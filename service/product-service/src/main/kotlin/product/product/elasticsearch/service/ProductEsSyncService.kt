package product.product.elasticsearch.service

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import product.product.domain.repository.ProductRepository
import product.product.elasticsearch.repository.ProductEsRepository
import product.product.elasticsearch.util.toDocument

@Service
class ProductEsSyncService(
    private val productRepository: ProductRepository, // R2DBC 쪽
    private val productEsRepository: ProductEsRepository
) {

    suspend fun initialLoad(pageSize: Int = 2000) {
        var page = 0L
        while (true) {
            val products = productRepository.findPageByOffset(page, pageSize).toList() // Flow<Product>
            if (products.isEmpty()) break

            val docs = products.map { it.toDocument() }
            productEsRepository.saveAll(docs)
            page++
        }
    }
}