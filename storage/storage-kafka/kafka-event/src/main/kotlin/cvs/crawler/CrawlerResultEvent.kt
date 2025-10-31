package cvs.crawler

data class CrawlerResultEvent(
    val target: CvsTarget,
    val data: String
)
