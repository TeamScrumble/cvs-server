package crawler.client.target

import cvs.crawler.CrawlerData
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

abstract class CVS {
    companion object {
        const val WAIT_TIMEOUT_SEC = 5L
        const val SLEEP_SHORT_MS = 800L
    }

    fun waitForElement(driver: WebDriver, selector: String, timeoutSec: Long = WAIT_TIMEOUT_SEC) {
        WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)))
    }

    fun scrollToBottom(driver: WebDriver) {
        (driver as JavascriptExecutor).executeScript("window.scrollTo(0, document.body.scrollHeight);")
        Thread.sleep(SLEEP_SHORT_MS)
    }

    fun run(headless: Boolean = false): List<CrawlerData> {
        val options = ChromeOptions().apply {
            if (headless) addArguments("--headless=new")
        }
        val driver = ChromeDriver(options)
        return try {
            crawl(driver)
        } catch (e: Exception) {
            println("크롤링 중 오류 발생: ${e.message}")
            emptyList()
        } finally {
            driver.quit()
        }
    }

    protected abstract fun crawl(driver: WebDriver): List<CrawlerData>

    protected abstract fun findProductList(driver: WebDriver): List<CrawlerData>
}