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
    private val operations: ElasticsearchOperations
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

    fun updateLikeCount(productId: Long, likeCount: Int) {
        val doc = Document.create().apply {
            put("likeCount", likeCount)
        }

        val query = UpdateQuery.builder(productId.toString())
            .withDocument(doc)
            .build()

        operations.update(query, operations.indexOps(ProductDocument::class.java).indexCoordinates)
    }
}