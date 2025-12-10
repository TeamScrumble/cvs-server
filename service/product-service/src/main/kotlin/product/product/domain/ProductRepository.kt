package product.product.domain

import cvs.crawler.CvsTarget
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long> {
    suspend fun findAllByCvsTarget(cvsTarget: CvsTarget): List<Product>
}
