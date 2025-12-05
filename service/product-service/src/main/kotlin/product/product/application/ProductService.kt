package product.product.application

import cvs.crawler.CrawlerResultEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import product.product.domain.Product
import product.product.domain.ProductRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    suspend fun save(result: CrawlerResultEvent) {
        val (target, productList) = result.target to result.data

        val entityList = productList.map { p ->
            Product(
                p.id.toLong(),
                target,
                p.productName,
                p.imgUrl,
                p.price,
                p.flag,
                p.isNew
            )
        }

        productRepository.saveAll(entityList).collect()
    }
}