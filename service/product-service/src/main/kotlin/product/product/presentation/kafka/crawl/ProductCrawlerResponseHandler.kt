package product.product.presentation.kafka.crawl

import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerResultEvent
import org.springframework.stereotype.Service
import product.product.application.service.ProductService
import product.product.elasticsearch.service.ProductEsSyncService
import java.util.*

@Service
class ProductCrawlerResponseHandler(
    private val productService: ProductService,
    private val productSyncService: ProductEsSyncService
) {
    suspend fun handleSuccess(result: CrawlerResultEvent) {
        val runId = UUID.randomUUID().toString()

        val savedProductIds = productService.save(result, crawlRunId = runId)
        productSyncService.upsertByProductIds(savedProductIds)

        val deletedProductIds = productService.softDeleteNotInRun(
            target = result.target,
            crawlRunId = runId
        )

        productSyncService.upsertByProductIds(deletedProductIds)
    }

    fun handleFail(event: CrawlerFailedEvent) {
        // TODO: 알람 발송
        // TODO: 장애 처리 로직
        // TODO: 재시도 정책 고려
    }
}