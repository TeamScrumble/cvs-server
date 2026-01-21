package product.product.application.service

import org.springframework.cache.CacheManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import product.product.ProductPopularSearchApi
import product.product.domain.repository.ProductSearchHistoryRepository
import product.product.domain.table.ProductSearchHistory
import java.time.LocalDateTime

@Service
class ProductSearchHistoryService(
    private val productSearchHistoryRepository: ProductSearchHistoryRepository,
    private val cacheManager: CacheManager
) {
    private val CACHE_NAME = "popularSearches"
    private val CACHE_KEY = "top10"

    /**
     * 검색어 저장: 캐시에 영향을 주지 않고 DB에만 기록합니다. (성능 최적화)
     */
    suspend fun saveSearchHistory(productTitle: String) {
        try {
            productSearchHistoryRepository.save(ProductSearchHistory(keyword = productTitle))
        } catch (e: Exception) {
            // 로깅 및 예외 무시
        }
    }

    /**
     * 인기 검색어 조회: 캐시된 데이터를 반환하며, 데이터가 없으면 즉시 업데이트 로직을 실행합니다.
     */
    suspend fun findPopularSearchesWithRanking(): List<ProductPopularSearchApi.PopularSearchItem> {
        val cache = cacheManager.getCache(CACHE_NAME)
        val cachedData = cache?.get(CACHE_KEY, List::class.java) as? List<ProductPopularSearchApi.PopularSearchItem>

        // 캐시가 비어있으면(Lazy Loading) 즉시 갱신 후 반환
        return cachedData ?: refreshPopularSearches()
    }

    suspend fun scheduledRefresh() {
        refreshPopularSearches()
    }

    /**
     * 실제 데이터를 비교하고 캐시를 갱신하는 핵심 비즈니스 로직
     */
    private suspend fun refreshPopularSearches(): List<ProductPopularSearchApi.PopularSearchItem> {
        val now = LocalDateTime.now()
        // 1. 현재 정시까지의 전체 누적 Top 10 조회
        val currentHourStart = now.withMinute(0).withSecond(0).withNano(0)
        val currentResults = productSearchHistoryRepository.findPopularSearches(
            until = currentHourStart,
            limit = 10
        )

        // 2. 이전에 캐싱되어 있던 데이터 가져오기 (비교용)
        val previousData = cacheManager.getCache(CACHE_NAME)?.get(CACHE_KEY, List::class.java)
                as? List<ProductPopularSearchApi.PopularSearchItem>

        // 3. 이전 순위 정보를 Map으로 변환 (상품명 -> 순위)
        val previousRankMap = previousData?.associate { it.productTitle to it.rank } ?: emptyMap()

        // 4. 순위 비교 및 결과 생성
        val finalResults = currentResults.mapIndexed { index, current ->
            val currentRank = index + 1
            val previousRank = previousRankMap[current.productTitle]

            val status = when {
                previousData == null -> ProductPopularSearchApi.ChangeStatus.SAME // 초기 기동 시
                previousRank == null -> ProductPopularSearchApi.ChangeStatus.NEW // 새로 진입
                currentRank < previousRank -> ProductPopularSearchApi.ChangeStatus.UP   // 순위 상승 (숫자가 작아짐)
                currentRank > previousRank -> ProductPopularSearchApi.ChangeStatus.DOWN // 순위 하락
                else -> ProductPopularSearchApi.ChangeStatus.SAME
            }

            ProductPopularSearchApi.PopularSearchItem(
                rank = currentRank,
                productTitle = current.productTitle,
                searchCount = current.searchCount,
                changeStatus = status,
                previousRank = previousRank,
                previousSearchCount = null // 누적 데이터라 이전 count 비교의 실익이 적으므로 null 혹은 필요시 추가
            )
        }

        // 5. 캐시 업데이트
        cacheManager.getCache(CACHE_NAME)?.put(CACHE_KEY, finalResults)

        return finalResults
    }
}