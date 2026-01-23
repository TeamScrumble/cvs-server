package product.product.domain.repository

import java.time.LocalDateTime

interface ProductSearchHistoryRepositoryCustom {
    suspend fun findPopularSearches(
        until: LocalDateTime?,
        limit: Int
    ): List<PopularSearchResult>

    data class PopularSearchResult(
        val productTitle: String,
        val searchCount: Long
    )
}

