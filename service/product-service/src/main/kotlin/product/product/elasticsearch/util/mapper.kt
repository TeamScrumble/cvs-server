package product.product.elasticsearch.util

import product.product.domain.table.Product
import product.product.elasticsearch.document.ProductDocument

fun productToDocument(p: Product) = ProductDocument(
    productId = p.id,
    cvsProductId = p.cvsProductId,
    cvsTarget = p.cvsTarget.name,
    title = p.title,
    img = p.img,
    price = p.price,
    event = p.event,
    isNewProduct = p.isNewProduct,
    likeCount = p.likeCount,
)