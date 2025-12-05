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
    private val scope = CoroutineScope(Dispatchers.IO)

    @Scheduled(cron = "0 0 2 * * TUE", zone = "Asia/Seoul")
    fun runWeeklyCrawlJob() {
        log.info("화요일 스케줄러 트리거됨 → 작업은 비동기로 진행")

        scope.launch {
            val targets = CvsTarget.entries

            for ((index, target) in targets.withIndex()) {
                val event = CrawlerRequestEvent(target)
                val payload = objectMapper.writeValueAsString(event)

                kafkaTemplate.send("crawl.request", payload)
                log.info("[$index] 발행 완료: $target")

                if (index < targets.lastIndex) {
                    delay(10 * 60 * 1000L)
                }
            }

            log.info("모든 타겟 순차 발행 완료")
        }
    }
}