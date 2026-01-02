package product.product.presentation.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CvsTarget
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import product.product.elasticsearch.service.ProductEsSyncService

@Component
class ProductSyncScheduler(
    private val productEsSyncService: ProductEsSyncService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 3 * * TUE")
    fun runWeeklySyncJob() = runBlocking {
        log.info("[Scheduler] RDB <=> ES 실행 완료")

        productEsSyncService.initialLoad(2000)

        log.info("[Scheduler] RDB <=> ES 전체 동기화 완료")
    }
}