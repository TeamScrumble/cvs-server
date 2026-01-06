package crawler.client.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerFailedEvent
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CrawlerResultEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class CrawlRequestConsumer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val crawlerService: CrawlerService
) {
    companion object {
        private const val CHUNK_SIZE = 800
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        logger.error("워크 스레드에서 처리되지 않은 예외 발생: ${ex.message}", ex)
    }

    private val workerScope = CoroutineScope(Dispatchers.IO + exceptionHandler)
    private val semaphore = Semaphore(4)

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["crawl.request"], groupId = "crawler-service")
    fun consume(message: String) {
        workerScope.launch {
            semaphore.withPermit {
                val event = objectMapper.readValue(message, CrawlerRequestEvent::class.java)
                val target = event.target

                logger.info("크롤 요청 수신: ${target.name}")

                try {
                    val crawled = crawlerService.crawl(target).chunked(CHUNK_SIZE)

                    crawled.forEachIndexed { chunkSeq, data ->
                        logger.info("크롤 요청 송신: [$chunkSeq] -> ${data.size}")

                        val result = CrawlerResultEvent(target, data)

                        kafkaTemplate.send("crawl.response", objectMapper.writeValueAsString(result))
                    }
                } catch (ex: Exception) {
                    val fail = CrawlerFailedEvent(target, ex.message ?: "fail")
                    kafkaTemplate.send("crawl.response.fail", objectMapper.writeValueAsString(fail))
                }
            }
        }
    }
}