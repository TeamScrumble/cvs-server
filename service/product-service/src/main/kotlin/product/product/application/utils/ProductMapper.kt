package product.product.application.utils

import cvs.crawler.CrawlerData
import cvs.crawler.CvsTarget
import product.product.ProductDto
import product.product.domain.table.Product
import java.util.zip.CRC32

internal fun Product.toResponse() = ProductDto(
    id, cvsProductId, cvsTarget.name, title, img, price, event, isNewProduct, likeCount
)

internal fun CrawlerData.toEntity(target: CvsTarget, crawlRunId: String, isDeleted: Boolean): Product {
    return Product(
        id = 0L,
        cvsProductId = this.id.toLong(),
        cvsTarget = target,
        title = this.productName,
        img = this.imgUrl,
        price = this.price,
        event = this.flag,
        isNewProduct = this.isNewProduct,
        isDeleted = isDeleted,
        crawlRunId = crawlRunId,
        likeCount = 0
    )
}

private fun generateId(input: String): String {
    val crc = CRC32()
    crc.update(input.toByteArray())
    return crc.value.toString()
}