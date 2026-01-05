package product.product.presentation.kafka.crawl

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerResultEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ProductCrawlResponseConsumer(
    private val objectMapper: ObjectMapper,
    private val responseHandler: ProductCrawlerResponseHandler
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        logger.error("응답 처리 중 처리되지 않은 예외 발생: ${ex.message}", ex)
    }

    private val workerScope = CoroutineScope(Dispatchers.IO + exceptionHandler)
    private val semaphore = Semaphore(4)

    /**
     * 크롤링 성공 응답 처리
     */
    @KafkaListener(topics = ["crawl.response"], groupId = "crawler-response")
    fun consumeSuccess(message: String) {
        workerScope.launch {
            semaphore.withPermit {
                try {
                    val event = objectMapper.readValue(message, CrawlerResultEvent::class.java)
                    logger.info("크롤링 성공 응답 수신: ${event.target}")

                    responseHandler.handleSuccess(event)
                } catch (ex: Exception) {
                    logger.error("crawl.response 처리 중 오류 발생: ${ex.message}", ex)
                }
            }
        }
    }

    /**
     * 크롤링 실패 응답 처리
     */
    @KafkaListener(topics = ["crawl.response.fail"], groupId = "crawler-response")
    fun consumeFail(message: String) {
        workerScope.launch {
            semaphore.withPermit {
                try {
                    val event = objectMapper.readValue(message, CrawlerFailedEvent::class.java)
                    logger.info("크롤링 실패 응답 수신: ${event.target}, reason=${event.message}")

                    responseHandler.handleFail(event)
                } catch (ex: Exception) {
                    logger.error("crawl.response.fail 처리 중 오류 발생: ${ex.message}", ex)
                }
            }
        }
    }
}