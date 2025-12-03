package crawler.client.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CrawlerResultEvent
import kotlinx.coroutines.runBlocking
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

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["crawl.request"], groupId = "crawler-service")
    fun consume(message: String) = runBlocking {
        val event = objectMapper.readValue(message, CrawlerRequestEvent::class.java)
        val target = event.target
        logger.info("크롤링 요청 수신: ${target.name}")

        val crawledData = crawlerService.crawl(target)

        val result = CrawlerResultEvent(target = target, data = crawledData)
        val json = objectMapper.writeValueAsString(result)

        kafkaTemplate.send("crawl.response", json)
        logger.info("크롤링 결과 발행 완료: $target")
    }
}