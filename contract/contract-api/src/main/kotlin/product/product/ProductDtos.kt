package product.product

import io.swagger.v3.oas.annotations.media.Schema

data class ProductDto(
    @Schema(description = "상품의 id", example = "10")
    val productId: Long,

    @Schema(description = "각 편의점 상품의 고유 id", example = "10")
    val cvsProductId: Long,

    @Schema(description = "편의점 종류", example = "GS25")
    val cvsTarget: String,

    @Schema(description = "상품명", example = "불닭마요 삼각김밥")
    val title: String,

    @Schema(description = "이미지 URL", example = "https://gs25.img/samgak.jpg")
    val img: String,

    @Schema(description = "가격", example = "1500")
    val price: Int,

    @Schema(description = "행사 정보", example = "1+1")
    val event: String,

    @Schema(description = "신상품 여부", example = "true")
    val isNewProduct: Boolean,

    @Schema(description = "좋아요 수", example = "10")
    val likeCount: Int,

    @Schema(description = "삭제 여부", example = "false")
    val isDeleted: Boolean,
)

data class ProductDocumentDto(
    @Schema(description = "상품의 id", example = "10")
    val productId: Long,

    @Schema(description = "편의점 종류", example = "GS25")
    val cvsTarget: String,

    @Schema(description = "상품명", example = "불닭마요 삼각김밥")
    val title: String,

    @Schema(description = "가격", example = "1500")
    val price: Int,

    @Schema(description = "행사 정보", example = "1+1")
    val event: String,

    @Schema(description = "신상품 여부", example = "true")
    val isNewProduct: Boolean,

    @Schema(description = "삭제 여부", example = "false")
    val isDeleted: Boolean,
)