package product.product.domain.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProductSearchHistoryRepositoryCustomImpl(
    private val client: DatabaseClient
) : ProductSearchHistoryRepositoryCustom {
    override suspend fun findPopularSearches(
        until: LocalDateTime?,
        limit: Int
    ): List<ProductSearchHistoryRepositoryCustom.PopularSearchResult> {
        val sql = if (until != null) {
            """
        SELECT keyword, COUNT(*) as search_count
        FROM product_search_history
        WHERE created_at < :until
        GROUP BY keyword
        ORDER BY search_count DESC, keyword ASC
        LIMIT :limit
        """.trimIndent()
        } else {
            """
        SELECT keyword, COUNT(*) as search_count
        FROM product_search_history
        GROUP BY keyword
        ORDER BY search_count DESC, keyword ASC
        LIMIT :limit
        """.trimIndent()
        }

        val query = client.sql(sql).let {
            if (until != null) it.bind("until", until) else it
        }.bind("limit", limit)

        return query
            .map { row, _ ->
                ProductSearchHistoryRepositoryCustom.PopularSearchResult(
                    productTitle = row.get("keyword", String::class.java)!!,
                    searchCount = (row.get("search_count") as Number).toLong()
                )
            }
            .all().collectList().awaitSingle()
    }
}

