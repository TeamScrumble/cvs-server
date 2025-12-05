package product.product.application

import cvs.crawler.CrawlerResultEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import product.product.domain.Product
import product.product.domain.ProductRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    suspend fun saveAll(results: List<CrawlerResultEvent>) = results.sumOf {
        save(it)
    }

    suspend fun save(result: CrawlerResultEvent): Int {
        val (target, productList) = result.target to result.data

        val entityList = productList.map { p ->
            Product(
                0L,
                p.id.toLong(),
                target,
                p.productName,
                p.imgUrl,
                p.price,
                p.flag,
                p.isNew
            )
        }

        return productRepository.saveAll(entityList).toList().size
    }
}