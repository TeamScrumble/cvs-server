package product.product.presentation.kafka.crawl

import cvs.crawler.CrawlResponseSuccessEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import messagebroker.base.KafkaEventBase
import messagebroker.consumer.EventConsumer
import org.springframework.stereotype.Component

@Component
class CrawlResponseSuccessEventConsumer(
    private val responseHandler: CrawlResponseEventHandler
) : EventConsumer<CrawlResponseSuccessEvent.Payload>, KafkaEventBase() {
    override fun consume(event: CrawlResponseSuccessEvent.Payload) {
        workerScope.launch {
            semaphore.withPermit {
                try {
                    logger.info("크롤링 성공 응답 수신: ${event.target} / 크롤링 데이터 : ${event.data.size}")
                    responseHandler.handleSuccess(event)
                } catch (ex: Exception) {
                    logger.error("crawl.response 처리 중 오류 발생: ${ex.message}", ex)
                }
            }
        }
    }
}