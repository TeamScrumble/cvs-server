package product.product.elasticsearch.service

import cvs.crawler.CvsTarget
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import product.product.elasticsearch.repository.ProductEsRepository
import product.product.elasticsearch.util.toDto

@Service
class ProductEsService(
    private val productEsRepository: ProductEsRepository
) {
    suspend fun findAllByKeyword(
        cvsTarget: CvsTarget,
        keyword: String,
        pageable: Pageable
    ) = productEsRepository.search(keyword, cvsTarget.name, pageable)
}