package product.product.application

import cvs.crawler.CrawlerData
import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import product.ProductAddApi
import java.util.zip.CRC32

fun List<ProductAddApi.Request>.toCrawlerResultDto() = groupBy {
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