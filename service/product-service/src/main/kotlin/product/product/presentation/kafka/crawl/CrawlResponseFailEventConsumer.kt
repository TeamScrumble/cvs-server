package product.product.presentation.kafka.crawl

import cvs.crawler.CrawlResponseFailEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import messagebroker.base.KafkaEventBase
import messagebroker.consumer.EventConsumer
import org.springframework.stereotype.Component

@Component
class CrawlResponseFailEventConsumer(
    private val responseHandler: CrawlResponseEventHandler
) : EventConsumer<CrawlResponseFailEvent.Payload>, KafkaEventBase() {
    override fun consume(event: CrawlResponseFailEvent.Payload) {
        workerScope.launch {
            semaphore.withPermit {
                try {
                    logger.info("크롤링 실패 응답 수신: ${event.target}, reason=${event.message}")
                    responseHandler.handleFail(event)
                } catch (ex: Exception) {
                    logger.error("crawl.response.fail 처리 중 오류 발생: ${ex.message}", ex)
                }
            }
        }
    }
}