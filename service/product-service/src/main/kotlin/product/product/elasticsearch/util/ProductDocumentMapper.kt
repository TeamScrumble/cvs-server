package product.product.elasticsearch.util

import product.product.ProductDto
import product.product.domain.table.Product
import product.product.elasticsearch.document.ProductDocument

fun Product.toDocument() = ProductDocument(
    productId = id,
    cvsTarget = cvsTarget.name,
    title = title,
    price = price,
    event = event,
    isNewProduct = isNewProduct,
    isDeleted = isDeleted
)