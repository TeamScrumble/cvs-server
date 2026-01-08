package product.product.presentation.kafka.crawl

import cvs.crawler.CrawlResponseFailEvent
import cvs.crawler.CrawlResponseSuccessEvent
import org.springframework.stereotype.Service
import product.product.application.service.ProductService
import product.product.elasticsearch.service.ProductEsSyncService
import java.util.*

@Service
class CrawlResponseEventHandler(
    private val productService: ProductService,
    private val productSyncService: ProductEsSyncService
) {
    suspend fun handleSuccess(result: CrawlResponseSuccessEvent.Payload) {
        if (result.data.isEmpty()) return

        val runId = result.runId

        val isFirstInit = productService.countAllByCvsTarget(result.target) == 0L

        val savedProductIds = productService.save(result, crawlRunId = runId)
        productSyncService.upsertByProductIds(savedProductIds)

        if (!isFirstInit) {
            val deletedProductIds = productService.softDeleteNotInRun(
                target = result.target,
                crawlRunId = runId
            )

            productSyncService.upsertByProductIds(deletedProductIds)
        }
    }

    fun handleFail(event: CrawlResponseFailEvent.Payload) {
        // TODO: 알람 발송
        // TODO: 장애 처리 로직
        // TODO: 재시도 정책 고려
    }
}