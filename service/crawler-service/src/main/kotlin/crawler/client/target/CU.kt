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
        private const val SELECTOR_PRODUCT_UL = "div.prodListWrap ul"
        private const val SELECTOR_MORE_BUTTON = ".prodListBtn .prodListBtn-w a"
    }

    override suspend fun findProductList(driver: WebDriver, selector: String): List<CrawlerData> {
        waitForElement(driver, selector)
        val items = driver.findElements(By.cssSelector(selector))
        if (items.isEmpty()) return emptyList()

        return items.map { item ->
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

    // ul 안의 li들을 통째로 비움 (ul 자체는 유지)
    private fun clearProductListDom(driver: WebDriver) {
        (driver as JavascriptExecutor).executeScript(
            """
            document.querySelectorAll(arguments[0]).forEach(ul => ul.innerHTML = '');
            """.trimIndent(),
            SELECTOR_PRODUCT_UL
        )
    }

    // 더보기(= nextPage) 한 번 실행. 기존 종료 조건(버튼 없거나 숨김/예외)이면 false
    private suspend fun clickMoreOnce(driver: WebDriver): Boolean {
        return try {
            delay(DELAY_BASIC_MS)

            val moreButton = WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_MORE_BUTTON)))

            if (!moreButton.isDisplayed) return false

            // onclick="nextPage(1)" 동일 동작
            (driver as JavascriptExecutor).executeScript("nextPage(1);")
            scrollToBottom(driver)
            true
        } catch (_: Exception) {
            false
        }
    }

    // 비워진 상태에서 로딩된 li가 다시 생길 때까지 짧게 대기
    private fun waitForItemsOrEmpty(driver: WebDriver, timeoutSeconds: Long = 3) {
        try {
            WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_PRODUCT_ITEM)))
        } catch (_: Exception) {
            // 아이템이 0개인 케이스도 있을 수 있으니 여기서는 그냥 통과
        }
    }

    override suspend fun crawl(driver: WebDriver): List<CrawlerData> = CATEGORIES.flatMap { categoryId ->
        driver.get(BASE_URL + categoryId)

        waitForElement(driver, SELECTOR_PRODUCT_ITEM)
        scrollToBottom(driver)

        val collected = mutableListOf<CrawlerData>()

        // (중요) 첫 페이지 누락 방지: 현재 떠있는 아이템 먼저 수집 → 비우기
        collected += runCatching { findProductList(driver, SELECTOR_PRODUCT_ITEM) }.getOrDefault(emptyList())
        clearProductListDom(driver)

        // 이후부터는 요청한 순서대로 반복: 더보기 → 수집 → 비우기
        while (true) {
            val clicked = clickMoreOnce(driver)
            if (!clicked) break

            waitForItemsOrEmpty(driver, timeoutSeconds = 3)

            val items = runCatching { findProductList(driver, SELECTOR_PRODUCT_ITEM) }
                .getOrDefault(emptyList())
            if (items.isNotEmpty()) {
                collected += items
            }

            clearProductListDom(driver)
        }

        collected
    }
}