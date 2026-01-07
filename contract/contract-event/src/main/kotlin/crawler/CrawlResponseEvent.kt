package cvs.crawler

import cvs.event.Event

data class CrawlerData(
    val id: String,
    val productName: String,
    val price: Int,
    val imgUrl: String,
    val flag: String,
    val isNewProduct: Boolean
)

object CrawlResponseSuccessEvent {
    const val TOPIC = "crawl.response"

    data class Payload(
        val target: CvsTarget,
        val data: List<CrawlerData>
    ) : Event
}

object CrawlResponseFailEvent {
    const val TOPIC = "crawl.response.fail"

    data class Payload(
        val target: CvsTarget,
        val message: String
    ) : Event
}