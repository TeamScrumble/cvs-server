package product.product.domain.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProductSearchHistoryRepositoryCustomImpl(
    private val client: DatabaseClient
) : ProductSearchHistoryRepositoryCustom {

    /**
     * 현재 시간의 정시를 기준으로 가장 많이 검색된 데이터를 가져옵니다.
     * 만약 동일한 랭킹이 있을 경우, 다음 로직에 따릅니다.
     * 1. 가장 최근에 검색이 된 데이터가 더 높은 순위를 차지합니다.
     * 2. 1번 마저도 동일한 경우, 이름순으로 정렬합니다.
     * */
    override suspend fun findPopularSearches(
        until: LocalDateTime?,
        limit: Int
    ): List<ProductSearchHistoryRepositoryCustom.PopularSearchResult> {
        val sql = """
        SELECT 
            keyword, 
            COUNT(*) as search_count, 
            MAX(created_at) as last_searched_at
        FROM product_search_history
        ${if (until != null) "WHERE created_at < :until" else ""}
        GROUP BY keyword
        ORDER BY 
            search_count DESC,
            last_searched_at DESC,
            keyword ASC
        LIMIT :limit
    """.trimIndent()

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

