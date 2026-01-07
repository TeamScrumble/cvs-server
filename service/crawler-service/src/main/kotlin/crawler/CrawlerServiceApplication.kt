package crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["crawler", "db", "security", "docs", "messagebroker"]
)
class CrawlerServerApplication

fun main(args: Array<String>) {
    runApplication<CrawlerServerApplication>(*args)
}