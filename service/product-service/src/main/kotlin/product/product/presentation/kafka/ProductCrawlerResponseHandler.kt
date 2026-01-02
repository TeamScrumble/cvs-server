package product.product.presentation.kafka

import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerResultEvent
import org.springframework.stereotype.Service
import product.product.application.service.ProductService
import product.product.elasticsearch.service.ProductEsSyncService

@Service
class ProductCrawlerResponseHandler(
    private val productService: ProductService,
    private val productSyncService: ProductEsSyncService
) {
    suspend fun handleSuccess(result: CrawlerResultEvent) {
        val savedProductIds = productService.save(result)
        productSyncService.upsertByProductIds(savedProductIds)
    }

    fun handleFail(event: CrawlerFailedEvent) {
        // TODO: 알람 발송
        // TODO: 장애 처리 로직
        // TODO: 재시도 정책 고려
    }
}