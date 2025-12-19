package crawler.client.target

import crawler.client.util.calculateTimeMillis
import cvs.crawler.CrawlerData
import cvs.crawler.CvsTarget
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.zip.CRC32
import kotlin.time.measureTimedValue

abstract class CVS {
    companion object {
        private val PRICE_REGEX = Regex("""\D""")
        const val NOT_EXIST_ID = "NOT_EXIST_ID"

        const val DELAY_SHORT_MS = 500L
        const val DELAY_BASIC_MS = 800L
        const val MAX_TIMEOUT_SEC = 5L
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    protected fun String.toPrice() = replace(PRICE_REGEX, "").toInt()

    protected fun generateId(input: String): String {
        val crc = CRC32()
        crc.update(input.toByteArray())
        return crc.value.toString()
    }

    fun waitForElement(driver: WebDriver, selector: String, timeoutSec: Long = MAX_TIMEOUT_SEC) {
        WebDriverWait(
            driver, Duration.ofSeconds(timeoutSec)
        ).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)))
    }

    suspend fun scrollToBottom(driver: WebDriver) {
        (driver as JavascriptExecutor).executeScript("window.scrollTo(0, document.body.scrollHeight);")
        delay(DELAY_BASIC_MS)
    }

    suspend fun run(target: CvsTarget, headless: Boolean = false): List<CrawlerData> {
        val prefix = "Crawling - $target"

        val options = ChromeOptions().apply {
            if (headless) addArguments("--headless=new")
        }
        val driver = ChromeDriver(options)

        return try {
            logger.info("[$prefix] 크롤링 시작")
            val (value, duration) = measureTimedValue {
                crawl(driver).distinctBy { it.id }
            }

            logger.info("[$prefix] 크롤링 종료 / 수집된 데이터 : ${value.size}개")

            calculateTimeMillis(logger, prefix, duration.inWholeMilliseconds)

            value
        } catch (e: Exception) {
            logger.warn("[$prefix] 크롤링 종료 / 에러 발생 : ${e.message}")
            emptyList()
        } finally {
            driver.quit()
        }
    }

    protected abstract suspend fun crawl(driver: WebDriver): List<CrawlerData>

    protected abstract suspend fun findProductList(driver: WebDriver, selector: String): List<CrawlerData>
}