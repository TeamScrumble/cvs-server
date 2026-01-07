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

@Component
class CrawlRequestEventConsumer(
    private val crawlerService: CrawlerService,
    private val eventPublisher: EventPublisher
) : EventConsumer<CrawlRequestEvent.Payload>, KafkaEventBase() {
    override fun consume(event: CrawlRequestEvent.Payload) {
        workerScope.launch {
            semaphore.withPermit {
                val target = event.target

                logger.info("[Crawl] consume event : ${target.name}")

                try {
                    val crawled = crawlerService.crawl(target).chunked(CHUNK_SIZE)

                    crawled.forEachIndexed { chunkSeq, data ->
                        logger.info("[Crawl] process completed event publish : [$chunkSeq] -> ${data.size}")

                        eventPublisher.publish(CrawlResponseSuccessEvent.Payload(target, data))
                    }
                } catch (ex: Exception) {
                    eventPublisher.publish(CrawlResponseFailEvent.Payload(target, ex.message ?: "fail to crawl"))
                }
            }
        }
    }
}