package crawler.client.target

import cvs.crawler.CrawlerData
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CU : CVS() {
    companion object {
        private val CATEGORIES = arrayOf(1, 2, 3, 4, 5, 6)
        private val ID_REGEX = Regex("""view\((\d+)\)""")
        private const val BASE_URL = "https://cu.bgfretail.com/product/product.do?category=product&depth2=4&depth3="

        private const val SELECTOR_PRODUCT_ITEM = "div.prodListWrap ul li"
        private const val SELECTOR_MORE_BUTTON = ".prodListBtn .prodListBtn-w a"
    }
    // ===== 상품 수집 =====
    override suspend fun findProductList(driver: WebDriver): List<CrawlerData> {
        waitForElement(driver, SELECTOR_PRODUCT_ITEM)
        val items = driver.findElements(By.cssSelector(SELECTOR_PRODUCT_ITEM))
        if (items.isEmpty()) {
            return emptyList()
        }

        return items.mapIndexed { idx, item ->
            val title = item.findElement(By.cssSelector(".name")).text.trim()
            val price = item.findElement(By.cssSelector(".price strong")).text.trim()
            val imgUrl = item.findElement(By.cssSelector(".prod_img img")).getDomAttribute("src") ?: ""

            val onClick = item.findElement(By.cssSelector(".prod_img"))
                .getDomAttribute("onclick") ?: ""

            val id = ID_REGEX.find(onClick)?.groupValues?.get(1) ?: NOT_EXIST_ID

            val flagElems = item.findElements(By.cssSelector(".badge span"))
            val newElems = item.findElements(By.cssSelector(".tag .new"))

            val flagText = flagElems.firstOrNull()?.text?.trim().orEmpty()
            val isNew = newElems.isNotEmpty()

            CrawlerData(id, title, price.toPrice(), imgUrl, flagText, isNew)
        }
    }

    // ===== '더보기' 버튼 처리 =====
    private suspend fun clickAllPages(driver: WebDriver) {
        var pageCount = 0
        while (true) {
            try {
                delay(DELAY_BASIC_MS)
                // 버튼이 DOM 상 존재하고 보일 때만 처리
                val moreButton = WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_MORE_BUTTON)))

                if (!moreButton.isDisplayed) {
                    break
                }

                pageCount += 1

                // onclick="nextPage(1)" 과 동일 동작
                (driver as JavascriptExecutor).executeScript("nextPage(1);")
                scrollToBottom(driver)
            } catch (_: Exception) {
                break
            }
        }
    }

    // ===== 전체 흐름 =====
    override suspend fun crawl(driver: WebDriver): List<CrawlerData> = CATEGORIES.flatMap { id ->
        driver.get(BASE_URL + id)

        waitForElement(driver, SELECTOR_PRODUCT_ITEM)

        scrollToBottom(driver)

        clickAllPages(driver)

        findProductList(driver)
    }
}