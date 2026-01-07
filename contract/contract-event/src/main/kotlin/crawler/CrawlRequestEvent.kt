package cvs.crawler

import cvs.event.Event

object CrawlRequestEvent {
    const val TOPIC = "crawl.request"

    data class Payload(
        val target: CvsTarget,
    ) : Event
}

data class CrawlerRequestEvent(
    val target: CvsTarget
)
