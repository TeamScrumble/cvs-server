package crawler.client.kafka

import cvs.crawler.CrawlResponseFailEvent
import cvs.crawler.CrawlRequestEvent
import cvs.crawler.CrawlResponseSuccessEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import messagebroker.base.KafkaEventBase
import messagebroker.consumer.EventConsumer
import messagebroker.publisher.EventPublisher
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 로컬에서는 환경에 따라 KafkaEventBase(4)로 수정해도 문제 없음
 * */
@Component
class CrawlRequestEventConsumer(
    private val crawlerService: CrawlerService,
    private val eventPublisher: EventPublisher
) : EventConsumer<CrawlRequestEvent.Payload>, KafkaEventBase(1) {
    override fun consume(event: CrawlRequestEvent.Payload) {
        workerScope.launch {
            semaphore.withPermit {
                val target = event.target

                logger.info("[Crawl] consume event : ${target.name}")

                try {
                    val runId = UUID.randomUUID().toString()

                    val crawled = crawlerService.crawl(target).chunked(CHUNK_SIZE)

                    crawled.forEachIndexed { chunkSeq, data ->
                        logger.info("[Crawl] process completed event publish : [$chunkSeq] -> ${data.size} / $runId")

                        eventPublisher.publish(CrawlResponseSuccessEvent.Payload(target, runId, data))
                    }
                } catch (ex: Exception) {
                    eventPublisher.publish(CrawlResponseFailEvent.Payload(target, ex.message ?: "fail to crawl"))
                }
            }
        }
    }
}