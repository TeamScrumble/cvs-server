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

@Component
class SevenEleven : CVS() {

    companion object {
        private const val BASE_URL = "https://www.7-eleven.co.kr/product/bestdosirakList.asp"
        private const val SELECTOR_PRODUCT_ITEM = "div.dosirak_list ul li"
        private const val SELECTOR_MORE_BUTTON = ".btn_more a"
    }

    // ===== 더보기 버튼 전체 클릭 =====
    private fun clickAllPages(driver: WebDriver) {
        var pageCount = 0

        while (true) {
            try {
                Thread.sleep(SLEEP_SHORT_MS)
                val wait = WebDriverWait(driver, Duration.ofSeconds(3))
                val moreButton = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_MORE_BUTTON))
                )

                if (!moreButton.isDisplayed) {
                    println("▶ 더보기 버튼이 숨겨졌습니다. 종료.")
                    break
                }

                pageCount++
                println("[$pageCount] 더보기 클릭 중...")

                (driver as JavascriptExecutor).executeScript("fncMore('');")
                scrollToBottom(driver)
            } catch (_: Exception) {
                println("▶ 더 이상 더보기 버튼이 없습니다. 종료.")
                break
            }
        }

        println("총 ${pageCount}회 '더보기' 실행 완료.")
    }

    // ===== 단일 상품 파싱 =====
    private fun parseProduct(item: WebElement): CrawlerData? {
        return try {
            val title = item.findElements(By.cssSelector(".pic_product .infowrap .name"))
                .firstOrNull()?.text?.trim().orEmpty()
            if (title.isBlank()) return null

            val price = item.findElements(By.cssSelector(".pic_product .infowrap .price span"))
                .firstOrNull()?.text?.trim().orEmpty()

            val imgUrl = item.findElements(By.cssSelector(".pic_product img"))
                .firstOrNull()?.getDomProperty("src").orEmpty()

            val isNew = item.findElements(By.cssSelector("ul.tag_list_01 .ico_tag_03")).isNotEmpty()

            CrawlerData(title, price, imgUrl, "", isNew)
        } catch (e: Exception) {
            println("상품 파싱 실패: ${e.message}")
            null
        }
    }

    // ===== 상품 리스트 추출 =====
    override fun findProductList(driver: WebDriver): List<CrawlerData> {
        waitForElement(driver, SELECTOR_PRODUCT_ITEM)
        val items = driver.findElements(By.cssSelector(SELECTOR_PRODUCT_ITEM))
        if (items.isEmpty()) {
            println("상품 항목이 없습니다.")
            return emptyList()
        }

        val products = items.mapNotNull { parseProduct(it) }
        println("상품 ${products.size}개 수집 완료.")
        return products
    }

    // ===== 전체 크롤링 흐름 =====
    override fun crawl(driver: WebDriver): List<CrawlerData> {
        driver.get(BASE_URL)
        waitForElement(driver, SELECTOR_PRODUCT_ITEM)
        scrollToBottom(driver)

        clickAllPages(driver)

        val productList = findProductList(driver)
        println("총 ${productList.size}개 상품 수집 완료.")
        return productList
    }
}