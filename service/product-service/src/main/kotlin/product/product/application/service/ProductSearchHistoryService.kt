package product.product.application.service

import org.springframework.stereotype.Service
import product.product.ProductPopularSearchApi
import product.product.domain.repository.ProductSearchHistoryRepository
import product.product.domain.table.ProductSearchHistory
import java.time.LocalDateTime

@Service
class ProductSearchHistoryService(
    private val productSearchHistoryRepository: ProductSearchHistoryRepository
) {
    /**
     * 상품 조회 시 검색어를 저장합니다.
     */
    suspend fun saveSearchHistory(productTitle: String) {
        try {
            productSearchHistoryRepository.save(
                ProductSearchHistory(
                    keyword = productTitle
                )
            )
        } catch (e: Exception) {
            // 로깅은 필요시 추가
            // 검색어 저장 실패가 전체 흐름에 영향을 주지 않도록 예외를 무시
        }
    }

    /**
     * 인기 검색어를 조회합니다.
     * 현재 시간의 정시를 기준으로 해당 시간대의 데이터를 조회하며, 이전 정시 구간과 비교하여 순위 변화를 계산합니다.
     * 
     * 예시:
     * - 14시 42분 → 13시~14시 구간 조회, 12시~13시 구간과 비교
     * - 13시 59분 → 12시~13시 구간 조회, 11시~12시 구간과 비교
     * - 00시 00분 → 전날 22시~23시 구간 조회, 전날 21시~22시 구간과 비교
     * - 00시 01분 → 전날 23시~00시 구간 조회, 전날 22시~23시 구간과 비교
     */
    suspend fun findPopularSearchesWithRanking(): List<ProductPopularSearchApi.PopularSearchItem> {
        val now = LocalDateTime.now()
        
        // 현재 시간의 정시 계산 (분, 초, 나노초를 0으로 설정)
        val currentHourStart = now.withMinute(0).withSecond(0).withNano(0)
        
        // 현재 정시 구간: (현재 정시 - 1시간) ~ 현재 정시
        // 예: 14시 42분 → 13시 00분 ~ 14시 00분
        val currentPeriodStart = currentHourStart.minusHours(1)
        val currentPeriodEnd = currentHourStart
        
        // 이전 정시 구간: (현재 정시 - 2시간) ~ (현재 정시 - 1시간)
        // 예: 14시 42분 → 12시 00분 ~ 13시 00분
        val previousPeriodStart = currentHourStart.minusHours(2)
        val previousPeriodEnd = currentHourStart.minusHours(1)

        // 현재 시점: 현재 정시 구간의 인기 검색어
        val currentResults = productSearchHistoryRepository.findPopularSearches(
            currentPeriodStart, 
            currentPeriodEnd, 
            10
        )

        // 이전 시점: 이전 정시 구간의 인기 검색어
        val previousResults = productSearchHistoryRepository.findPopularSearches(
            previousPeriodStart, 
            previousPeriodEnd, 
            10
        )

        // 이전 시점의 데이터를 맵으로 변환 (상품명 -> (순위, 검색횟수))
        val previousMap = previousResults.mapIndexed { index, result ->
            result.productTitle to (index + 1 to result.searchCount)
        }.toMap()

        // 현재 시점의 데이터를 순위와 함께 변환
        return currentResults.mapIndexed { index, current ->
            val currentRank = index + 1
            val previousData = previousMap[current.productTitle]

            val changeStatus = when {
                previousData == null -> {
                    // 이전 시점에 없었던 경우: NEW
                    ProductPopularSearchApi.ChangeStatus.NEW
                }
                current.searchCount > previousData.second -> {
                    // 검색 횟수 증가: UP
                    ProductPopularSearchApi.ChangeStatus.UP
                }
                current.searchCount < previousData.second -> {
                    // 검색 횟수 감소: DOWN
                    ProductPopularSearchApi.ChangeStatus.DOWN
                }
                currentRank < previousData.first -> {
                    // 검색 횟수는 같지만 순위 상승: UP
                    ProductPopularSearchApi.ChangeStatus.UP
                }
                currentRank > previousData.first -> {
                    // 검색 횟수는 같지만 순위 하락: DOWN
                    ProductPopularSearchApi.ChangeStatus.DOWN
                }
                else -> {
                    // 순위와 검색 횟수 모두 동일: SAME
                    ProductPopularSearchApi.ChangeStatus.SAME
                }
            }

            ProductPopularSearchApi.PopularSearchItem(
                rank = currentRank,
                productTitle = current.productTitle,
                searchCount = current.searchCount,
                changeStatus = changeStatus,
                previousRank = previousData?.first,
                previousSearchCount = previousData?.second
            )
        }
    }
}

