package crawler.client.service

import crawler.client.target.CU
import crawler.client.target.Emart24
import crawler.client.target.GS25
import crawler.client.target.SevenEleven
import org.springframework.stereotype.Service

@Service
class CrawlerService(
    private val cu: CU,
    private val gs25: GS25,
    private val emart24: Emart24,
    private val sevenEleven: SevenEleven,
) {
    fun crawl(url: String) {
        listOf(
            cu to "CU",
            sevenEleven to "7-Eleven",
            gs25 to "GS25",
            emart24 to "Emart24"
        ).forEach { (crawler, name) ->
            println("[$name] 크롤링 시작")
            crawler.run(false)
            println("[$name] 크롤링 완료")
        }
    }
}