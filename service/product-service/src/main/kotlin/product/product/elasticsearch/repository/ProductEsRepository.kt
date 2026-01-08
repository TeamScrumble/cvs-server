package product.product.elasticsearch.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import product.product.elasticsearch.document.ProductDocument

interface ProductEsRepository : ElasticsearchRepository<ProductDocument, Long> {
    @Query(
        """
        {
          "bool": {
            "must": [
              { "match": { "title": { "query": "?0", "zero_terms_query": "all" } } }
            ],
            "filter": [
              { "term": { "isDeleted": false } }
            ]
          }
        }
        """
    )
    fun searchAllExcludeDeletedProduct(q: String, pageable: Pageable): Page<ProductDocument>

    @Query(
        """
        {
          "bool": {
            "must": [
              { "match": { "title": { "query": "?0", "zero_terms_query": "all" } } }
            ]
          }
        }
        """
    )
    fun searchAll(q: String, pageable: Pageable): Page<ProductDocument>

    @Query(
        """
        {
          "bool": {
            "must": [
              { "match": { "title": { "query": "?0", "zero_terms_query": "all" } } }
            ],
            "filter": [
              { "term": { "cvsTarget": "?1" } }
            ]
          }
        }
        """
    )
    fun searchByTarget(q: String, cvsTarget: String, pageable: Pageable): Page<ProductDocument>

    @Query(
        """
        {
          "bool": {
            "must": [
              { "match": { "title": { "query": "?0", "zero_terms_query": "all" } } }
            ],
            "filter": [
              { "term": { "cvsTarget": "?1" } },
              { "term": { "isDeleted": false } }
            ]
          }
        }
        """
    )
    fun searchByTargetExcludeDeletedProduct(q: String, cvsTarget: String, pageable: Pageable): Page<ProductDocument>
}