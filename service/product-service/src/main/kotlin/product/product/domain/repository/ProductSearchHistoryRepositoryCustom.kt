package product.product.domain.repository

import java.time.LocalDateTime

interface ProductSearchHistoryRepositoryCustom {
    suspend fun findPopularSearches(
        since: LocalDateTime,
        limit: Int
    ): List<PopularSearchResult>

    suspend fun findPopularSearches(
        since: LocalDateTime,
        until: LocalDateTime?,
        limit: Int
    ): List<PopularSearchResult>

    data class PopularSearchResult(
        val productTitle: String,
        val searchCount: Long
    )
}

