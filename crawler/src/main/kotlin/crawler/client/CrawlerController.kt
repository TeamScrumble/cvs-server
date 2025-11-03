package crawler.client

import com.fasterxml.jackson.databind.ObjectMapper
import crawler.client.kafka.CrawlerService
import crawler.client.util.calculateTimeMillis
import cvs.crawler.CrawlerData
import cvs.crawler.CvsTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

@RestController
class CrawlerController(
    val sv: CrawlerService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    val inMemoryDB = mutableMapOf<CvsTarget, List<CrawlerData>>()
    @PostMapping("/crawl")
    suspend fun net() = coroutineScope {
        val results = CvsTarget.entries.map { target ->
            async(Dispatchers.IO) {
                val result = measureTimedValue {
                    sv.crawl(target)
                }

                inMemoryDB[target] = result.value
            }
        }

        val duration = measureTime {
            results.awaitAll()
        }

        calculateTimeMillis(logger, "Crawling - 전체", duration.inWholeMilliseconds)

        inMemoryDB
    }
}