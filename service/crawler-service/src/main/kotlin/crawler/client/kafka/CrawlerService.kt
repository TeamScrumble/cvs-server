package crawler.client.kafka

import crawler.client.target.CU
import crawler.client.target.Emart24
import crawler.client.target.GS25
import crawler.client.target.SevenEleven
import cvs.crawler.CvsTarget
import org.springframework.stereotype.Service

@Service
class CrawlerService(
    private val cu: CU,
    private val gs25: GS25,
    private val emart24: Emart24,
    private val sevenEleven: SevenEleven,
) {
    private val crawlers = mapOf(
        CvsTarget.CU to cu,
        CvsTarget.GS25 to gs25,
        CvsTarget.EMART_24 to emart24,
        CvsTarget.SEVEN_ELEVEN to sevenEleven
    )
    suspend fun crawl(target: CvsTarget) = crawlers[target]?.run(target, false) ?: emptyList()
}