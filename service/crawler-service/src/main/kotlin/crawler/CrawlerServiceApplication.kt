package crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrawlerServerApplication

fun main(args: Array<String>) {
    runApplication<CrawlerServerApplication>(*args)
}