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
              {
                "multi_match": {
                  "query": "?0",
                  "fields": ["title", "title.ngram"],
                  "operator": "and"
                }
              }
            ],
            "filter": [
              { "term": { "isDeleted": false } }
            ],
            "should": [
              {
                "match_phrase": { "title": { "query": "?0", "boost": 100 } }
              },
              {
                "term": { "title.keyword": { "value": "?0", "boost": 200 } }
              }
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
              {
                "multi_match": {
                  "query": "?0",
                  "fields": ["title", "title.ngram"],
                  "operator": "and"
                }
              }
            ],
            "filter": [
              { "term": { "cvsTarget": "?1" } },
              { "term": { "isDeleted": false } }
            ],
            "should": [
              {
                "match_phrase": { "title": { "query": "?0", "boost": 100 } }
              },
              {
                "term": { "title.keyword": { "value": "?0", "boost": 200 } }
              }
            ]
          }
        }
        """
    )
    fun searchByTarget(q: String, cvsTarget: String, pageable: Pageable): Page<ProductDocument>
}