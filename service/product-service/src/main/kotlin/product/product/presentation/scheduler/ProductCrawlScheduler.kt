package product.product.presentation.scheduler

import cvs.crawler.CrawlRequestEvent
import cvs.crawler.CvsTarget
import messagebroker.publisher.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductCrawlScheduler(
    private val eventPublisher: EventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 2 * * TUE")
    fun runWeeklyCrawlJob() {
        log.info("[Scheduler] 실행 완료")

        CvsTarget.entries.forEach { target ->
            eventPublisher.publish(CrawlRequestEvent.Payload(target))
            log.info("[Scheduler] $target 티켓 발행 완료")
        }

        log.info("[Scheduler] 전체 티켓 발행 완료")
    }
}