package cvs.crawler

data class CrawlerResultEvent(
    val target: CvsTarget,
    val data: String
)

data class CrawlerData(
    val id: String,
    val productName: String,
    val price: Int,
    val imgUrl: String,
    val flag: String,
    val isNew: Boolean
)