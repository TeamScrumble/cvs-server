package product.product.elasticsearch.util

import product.product.ProductDto
import product.product.domain.table.Product
import product.product.elasticsearch.document.ProductDocument

fun Product.toDocument() = ProductDocument(
    productId = id,
    cvsProductId = cvsProductId,
    cvsTarget = cvsTarget.name,
    title = title,
    img = img,
    price = price,
    event = event,
    isNewProduct = isNewProduct,
    likeCount = likeCount,
    isDeleted = isDeleted
)

fun ProductDocument.toDto() = ProductDto(
    productId = productId,
    cvsProductId = cvsProductId,
    cvsTarget = cvsTarget,
    title = title,
    img = img,
    price = price,
    event = event,
    isNewProduct = isNewProduct,
    likeCount = likeCount,
)