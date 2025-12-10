package crawler.client.target

import cvs.crawler.CrawlerData
import cvs.crawler.CvsTarget
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.springframework.stereotype.Component

@Component
class Emart24 : CVS() {

    companion object {
        private const val BASE_URL = "https://emart24.co.kr/goods/event"
        private const val SELECTOR_ITEM = ".itemList .itemWrap"
        private const val SELECTOR_DOUBLE_NEXT = ".pageNationWrap .doubleNext"
        private const val SELECTOR_NEXT_BTN = ".nextButtons .next"
        private const val SELECTOR_PAGE_FOCUS = ".pageNationWrap .pIndex.focus span"

        private val PRODUCT_IMG_URL_REGEX = Regex(""".*/([0-9]+)\.[A-Za-z0-9]+$""")

        private val EVENT_MAPPING = mapOf(
            "onepl" to "1+1",
            "twopl" to "2+1",
            "gola" to "" // 골라담기 제외
        )
    }

    // ===== 상품 파싱 =====
    override suspend fun findProductList(driver: WebDriver): List<CrawlerData> {
        val items = driver.findElements(By.cssSelector(SELECTOR_ITEM))
        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val title = item.findElement(By.cssSelector(".itemTxtWrap .itemtitle p a")).text.trim()
            val price = item.findElement(By.cssSelector(".itemTxtWrap .price")).text.trim()
            val imgUrl = item.findElement(By.cssSelector(".itemSpImg img")).getDomAttribute("src") ?: ""

            // 이벤트 유형 파싱
            val eventElems = item.findElements(By.cssSelector(".itemTit .floatR"))
            val eventText = eventElems.firstOrNull()?.getDomAttribute("class")?.let { className ->
                EVENT_MAPPING.entries.firstOrNull { className.contains(it.key) }?.value
            }.orEmpty()

            val productImgId = PRODUCT_IMG_URL_REGEX.find(imgUrl)?.groupValues?.get(1) ?: NOT_EXIST_ID

            val id = generateId("${CvsTarget.EMART_24.name}|$title")

            CrawlerData(id, title, price.toPrice(), imgUrl, eventText, false)
        }
    }

    // ===== 마지막 페이지 탐색 =====
    private suspend fun findLastPageNumber(driver: WebDriver): Int {
        waitForElement(driver, SELECTOR_DOUBLE_NEXT)

        while (true) {
            val doubleNext = driver.findElement(By.cssSelector(SELECTOR_DOUBLE_NEXT))
            val opacity = doubleNext.getCssValue("opacity").toDoubleOrNull() ?: 1.0

            if (opacity <= 0.3) break

            (driver as JavascriptExecutor).executeScript("arguments[0].click();", doubleNext)
            waitForElement(driver, SELECTOR_PAGE_FOCUS)
            delay(DELAY_BASIC_MS)
        }

        val lastPage = driver.findElement(By.cssSelector(SELECTOR_PAGE_FOCUS)).text.trim().toInt()
        return lastPage
    }

    // ===== 페이지 이동 처리 =====
    private suspend fun goToNextPage(driver: WebDriver): Boolean = try {
        val nextBtn = driver.findElement(By.cssSelector(SELECTOR_NEXT_BTN))
        val opacity = nextBtn.getCssValue("opacity").toDoubleOrNull() ?: 1.0

        if (opacity <= 0.3) {
            false
        } else {
            (driver as JavascriptExecutor).executeScript("arguments[0].click();", nextBtn)
            waitForElement(driver, SELECTOR_ITEM)
            scrollToBottom(driver)
            true
        }
    } catch (e: Exception) {
        false
    }

    // ===== 전체 크롤링 흐름 =====
    override suspend fun crawl(driver: WebDriver): List<CrawlerData> {
        driver.get(BASE_URL)
        waitForElement(driver, SELECTOR_ITEM)
        scrollToBottom(driver)

        val lastPage = findLastPageNumber(driver)

        // 첫 페이지로 복귀
        driver.get(BASE_URL)
        waitForElement(driver, SELECTOR_ITEM)
        scrollToBottom(driver)

        val allProducts = mutableListOf<CrawlerData>()
        var pageNum = 1

        while (true) {
            val productList = findProductList(driver)

            if (productList.isEmpty()) {
                break
            }

            allProducts += productList

            if (!goToNextPage(driver)) break
            delay(500L)

            pageNum++
        }

        return allProducts
    }
}