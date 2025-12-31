package product.product.application.utils

import cvs.crawler.CrawlerData
import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import product.product.ProductAddApi
import product.product.ProductDto
import product.product.domain.table.Product
import java.util.zip.CRC32

internal fun Product.toResponse() = ProductDto(
    id, cvsProductId, cvsTarget.name, title, img, price, event, isNewProduct, likeCount
)

internal fun CrawlerData.toEntity(target: CvsTarget): Product {
    return Product(
        id = 0L,
        cvsProductId = this.id.toLong(),
        cvsTarget = target,
        title = this.productName,
        img = this.imgUrl,
        price = this.price,
        event = this.flag,
        isNewProduct = this.isNew,
        likeCount = 0
    )
}

internal fun List<ProductAddApi.Request>.toCrawlerResultDto() = groupBy {
    CvsTarget.valueOf(it.cvsTarget)
}.map { (key, value) ->
    CrawlerResultEvent(key, value.map { v ->
        CrawlerData(
            generateId("${key}|${v.title}"),
            v.title,
            v.price,
            v.img,
            v.event,
            v.isNew
        )
    })
}

private fun generateId(input: String): String {
    val crc = CRC32()
    crc.update(input.toByteArray())
    return crc.value.toString()
}