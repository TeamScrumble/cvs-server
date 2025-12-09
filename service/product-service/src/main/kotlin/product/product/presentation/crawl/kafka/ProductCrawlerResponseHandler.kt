package product.product.presentation.crawl.kafka

import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerResultEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import product.product.application.ProductService

@Service
class ProductCrawlerResponseHandler(
    private val productService: ProductService
) {
    suspend fun handleSuccess(result: CrawlerResultEvent) {
        productService.save(result)
    }

    fun handleFail(event: CrawlerFailedEvent) {
        // TODO: 알람 발송
        // TODO: 장애 처리 로직
        // TODO: 재시도 정책 고려
    }
}