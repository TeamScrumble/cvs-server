package product.product.presentation.scheduler

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import product.product.application.service.ProductService

@Component
class ProductSyncScheduler(
    private val productService: ProductService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    fun runWeeklySyncJob() = runBlocking {
        log.info("[Scheduler] RDB <=> ES 실행 완료")

        productService.sync(0)

        log.info("[Scheduler] RDB <=> ES 전체 동기화 완료")
    }
}