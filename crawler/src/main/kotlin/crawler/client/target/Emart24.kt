package crawler.client.target

import cvs.crawler.CrawlerData
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

        private val EVENT_MAPPING = mapOf(
            "onepl" to "1+1",
            "twopl" to "2+1",
            "gola" to "" // 골라담기 제외
        )
    }

    // ===== 상품 파싱 =====
    override fun findProductList(driver: WebDriver): List<CrawlerData> {
        val items = driver.findElements(By.cssSelector(SELECTOR_ITEM))
        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val title = item.findElement(By.cssSelector(".itemTxtWrap .itemtitle p a")).text.trim()
            val price = item.findElement(By.cssSelector(".itemTxtWrap .price")).text.trim()
            val imgUrl = item.findElement(By.cssSelector(".itemSpImg img")).getDomProperty("src") ?: ""

            // 이벤트 유형 파싱
            val eventElems = item.findElements(By.cssSelector(".itemTit .floatR"))
            val eventText = eventElems.firstOrNull()?.getDomProperty("class")?.let { className ->
                EVENT_MAPPING.entries.firstOrNull { className.contains(it.key) }?.value
            }.orEmpty()

            CrawlerData(title, price, imgUrl, eventText, false)
        }
    }

    // ===== 마지막 페이지 탐색 =====
    private fun findLastPageNumber(driver: WebDriver): Int {
        waitForElement(driver, SELECTOR_DOUBLE_NEXT)

        while (true) {
            val doubleNext = driver.findElement(By.cssSelector(SELECTOR_DOUBLE_NEXT))
            val opacity = doubleNext.getCssValue("opacity").toDoubleOrNull() ?: 1.0

            if (opacity <= 0.3) break

            (driver as JavascriptExecutor).executeScript("arguments[0].click();", doubleNext)
            waitForElement(driver, SELECTOR_PAGE_FOCUS)
            Thread.sleep(SLEEP_SHORT_MS)
        }

        val lastPage = driver.findElement(By.cssSelector(SELECTOR_PAGE_FOCUS)).text.trim().toInt()
        println("▶ 마지막 페이지 번호: $lastPage")
        return lastPage
    }

    // ===== 페이지 이동 처리 =====
    private fun goToNextPage(driver: WebDriver): Boolean {
        return try {
            val nextBtn = driver.findElement(By.cssSelector(SELECTOR_NEXT_BTN))
            val opacity = nextBtn.getCssValue("opacity").toDoubleOrNull() ?: 1.0

            if (opacity <= 0.3) {
                println("▶ 마지막 페이지 도달")
                false
            } else {
                (driver as JavascriptExecutor).executeScript("arguments[0].click();", nextBtn)
                waitForElement(driver, SELECTOR_ITEM)
                scrollToBottom(driver)
                true
            }
        } catch (e: Exception) {
            println("다음 페이지 이동 실패: ${e.message}")
            false
        }
    }

    // ===== 전체 크롤링 흐름 =====
    override fun crawl(driver: WebDriver): List<CrawlerData> {
        driver.get(BASE_URL)
        waitForElement(driver, SELECTOR_ITEM)
        scrollToBottom(driver)

        val lastPage = findLastPageNumber(driver)
        println("총 ${lastPage}페이지 탐색 시작\n")

        // 첫 페이지로 복귀
        driver.get(BASE_URL)
        waitForElement(driver, SELECTOR_ITEM)
        scrollToBottom(driver)

        val allProducts = mutableListOf<CrawlerData>()
        var pageNum = 1

        while (true) {
            println("=== [Page $pageNum/$lastPage] ===")
            val productList = findProductList(driver)

            if (productList.isEmpty()) {
                println("상품이 없습니다. 종료.")
                break
            }

            println("상품 ${productList.size}개 수집 완료.")
            allProducts += productList

            if (!goToNextPage(driver)) break
            pageNum++
        }

        println("총 ${allProducts.size}개 상품 수집 완료.")
        return allProducts
    }
}