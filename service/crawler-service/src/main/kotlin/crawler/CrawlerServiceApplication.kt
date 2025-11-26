package crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["crawler", "docs"])
class CrawlerServerApplication

fun main(args: Array<String>) {
    runApplication<CrawlerServerApplication>(*args)
}