package product

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Product", description = "상품 API")
interface ProductApi : ProductAddApi, ProductGetApi