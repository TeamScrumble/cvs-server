package product.product.presentation.crawl.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CvsTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductCrawlScheduler(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 2 * * TUE")
    fun runWeeklyCrawlJob() {
        log.info("[Scheduler] 실행 완료")

        CvsTarget.entries.forEach { target ->
            val payload = objectMapper.writeValueAsString(CrawlerRequestEvent(target))
            kafkaTemplate.send("crawl.request", payload)
            log.info("[Scheduler] $target 티켓 발행 완료")
        }

        log.info("[Scheduler] 전체 티켓 발행 완료")
    }
}