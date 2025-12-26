package product.product.domain.repository

import cvs.crawler.CvsTarget
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import product.product.domain.table.Product

interface ProductRepository : CoroutineCrudRepository<Product, Long> {
    suspend fun findAllByCvsTarget(cvsTarget: CvsTarget): List<Product>
}
