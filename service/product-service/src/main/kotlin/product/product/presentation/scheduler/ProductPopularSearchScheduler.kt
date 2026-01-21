package product.product.presentation.scheduler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import product.product.application.service.ProductSearchHistoryService

@Component
class ProductPopularSearchScheduler(
    private val productSearchHistoryService: ProductSearchHistoryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매 정시(00분 00초)마다 캐시를 업데이트하는 배치 로직
     */
    @Scheduled(cron = "0 0 * * * *")
    suspend fun scheduledRefresh() {
        log.info("[PopularSearch] Refresh start")
        productSearchHistoryService.scheduledRefresh()
        log.info("[PopularSearch] Refresh end")
    }
}