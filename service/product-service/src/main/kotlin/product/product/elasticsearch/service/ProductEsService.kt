package product.product.elasticsearch.service

import cvs.crawler.CvsTarget
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.query.UpdateQuery
import org.springframework.stereotype.Service
import product.product.elasticsearch.document.ProductDocument
import product.product.elasticsearch.repository.ProductEsRepository

@Service
class ProductEsService(
    private val productEsRepository: ProductEsRepository,
) {
    suspend fun findAllByKeyword(
        cvsTarget: CvsTarget?,
        keyword: String,
        pageable: Pageable
    ) = if (cvsTarget == null) {
        productEsRepository.searchAll(keyword, pageable)
    } else {
        productEsRepository.searchByTarget(keyword, cvsTarget.name, pageable)
    }
}