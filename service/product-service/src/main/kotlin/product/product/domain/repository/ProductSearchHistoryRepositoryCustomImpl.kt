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
        since: LocalDateTime,
        limit: Int
    ): List<ProductSearchHistoryRepositoryCustom.PopularSearchResult> {
        return findPopularSearches(since, null, limit)
    }

    override suspend fun findPopularSearches(
        since: LocalDateTime,
        until: LocalDateTime?,
        limit: Int
    ): List<ProductSearchHistoryRepositoryCustom.PopularSearchResult> {
        val sql = if (until != null) {
            """
            SELECT keyword, COUNT(*) as search_count
            FROM product_search_history
            WHERE created_at >= :since AND created_at < :until
            GROUP BY keyword
            ORDER BY search_count DESC, keyword ASC
            LIMIT :limit
            """.trimIndent()
        } else {
            """
            SELECT keyword, COUNT(*) as search_count
            FROM product_search_history
            WHERE created_at >= :since
            GROUP BY keyword
            ORDER BY search_count DESC, keyword ASC
            LIMIT :limit
            """.trimIndent()
        }

        var query = client.sql(sql)
            .bind("since", since)
            .bind("limit", limit)

        if (until != null) {
            query = query.bind("until", until)
        }

        return query
            .map { row, _ ->
                ProductSearchHistoryRepositoryCustom.PopularSearchResult(
                    productTitle = row.get("keyword", String::class.java)!!,
                    searchCount = (row.get("search_count") as Number).toLong()
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }
}

