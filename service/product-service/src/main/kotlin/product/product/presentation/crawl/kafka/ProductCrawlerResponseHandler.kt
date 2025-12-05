package product.product.presentation.crawl.kafka

import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerResultEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductCrawlerResponseHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun handleSuccess(result: CrawlerResultEvent) {
        // TODO: DB 저장
    }

    fun handleFail(event: CrawlerFailedEvent) {
        // TODO: 알람 발송
        // TODO: 장애 처리 로직
        // TODO: 재시도 정책 고려
    }
}