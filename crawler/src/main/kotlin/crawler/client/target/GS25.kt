package crawler.client.target

import cvs.crawler.CrawlerData
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.regex.Pattern

@Component
class GS25 : CVS() {

    companion object {
        private const val BASE_URL = "http://gs25.gsretail.com/gscvs/ko/products/event-goods#;"
        private const val SELECTOR_ITEM = "ul.prod_list li"
        private const val SELECTOR_NEXT2 = ".paging .next2"
        private const val SELECTOR_TAB_TOTAL = "#TOTAL"
        private const val WAIT_SHORT_MS = 500L
    }

    // ===== 페이지 이동 =====
    private fun moveToPage(driver: WebDriver, pageNum: Int) {
        (driver as JavascriptExecutor).executeScript("goodsPageController.movePage($pageNum);")
        waitForElement(driver, SELECTOR_ITEM)
        Thread.sleep(WAIT_SHORT_MS)
        println("▶ ${pageNum}페이지 이동 완료.")
    }

    // ===== 마지막 페이지 번호 추출 =====
    private fun getLastPageNumber(driver: WebDriver): Int {
        return try {
            val wait = WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SEC))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_NEXT2)))

            val next2Elem = driver.findElement(By.cssSelector(SELECTOR_NEXT2))
            val onclickAttr = next2Elem.getAttribute("onclick") ?: return 1

            val matcher = Pattern.compile("movePage\\((\\d+)\\)").matcher(onclickAttr)
            if (matcher.find()) matcher.group(1)?.toIntOrNull() ?: 1 else 1
        } catch (e: Exception) {
            println("마지막 페이지 번호 추출 실패: ${e.message}")
            1
        }
    }

    // ===== 상품 파싱 =====
    private fun parseProduct(item: WebElement): CrawlerData? {
        return try {
            val title = item.findElements(By.cssSelector(".tit")).firstOrNull()?.text?.trim().orEmpty()
            if (title.isBlank()) return null

            val price = item.findElements(By.cssSelector(".price")).firstOrNull()?.text?.trim().orEmpty()
            val imgUrl = item.findElements(By.cssSelector("img")).firstOrNull()?.getDomProperty("src").orEmpty()
            val flagText = item.findElements(By.cssSelector(".flag_box span")).firstOrNull()?.text?.trim().orEmpty()

            CrawlerData(title, price, imgUrl, flagText, false)
        } catch (e: Exception) {
            println("상품 파싱 실패: ${e.message}")
            null
        }
    }

    // ===== 상품 리스트 수집 =====
    override fun findProductList(driver: WebDriver): List<CrawlerData> {
        waitForElement(driver, SELECTOR_ITEM)
        Thread.sleep(WAIT_SHORT_MS)

        val items = driver.findElements(By.cssSelector(SELECTOR_ITEM))
        if (items.isEmpty()) {
            println("상품이 없습니다.")
            return emptyList()
        }

        val products = items.mapNotNull { parseProduct(it) }
        println("상품 ${products.size}개 수집 완료.")
        return products
    }

    // ===== 전체 크롤링 흐름 =====
    override fun crawl(driver: WebDriver): List<CrawlerData> {
        driver.get(BASE_URL)

        // "전체" 탭 클릭
        WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SEC))
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector(SELECTOR_TAB_TOTAL)))
        driver.findElement(By.cssSelector(SELECTOR_TAB_TOTAL)).click()

        Thread.sleep(WAIT_SHORT_MS)

        // 페이지 수 확인
        val lastPage = getLastPageNumber(driver)
        println("총 ${lastPage}페이지 탐색 시작\n")

        val allProducts = mutableListOf<CrawlerData>()

        for (pageNum in 1..lastPage) {
            println("=== [Page $pageNum/$lastPage] ===")
            moveToPage(driver, pageNum)

            val productList = findProductList(driver)
            if (productList.isEmpty()) break

            allProducts += productList
        }

        println("총 ${allProducts.size}개 상품 수집 완료.")
        return allProducts
    }
}