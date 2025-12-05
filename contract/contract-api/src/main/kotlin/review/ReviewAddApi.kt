package review

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ReviewAddApi {
    companion object {
        const val PATH = "/api/product/review"
    }

    @Documented(
        summary = "상품 리뷰 등록 API",
        description = "상품 리뷰를 등록하는 API",
        request = Request::class,
        response = Response::class
    )
    suspend fun add(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "리뷰를 등록할 상품 id", example = "1")
        val productId: Long,
        @Schema(description = "리뷰를 등록한 회원 id", example = "1")
        val memberId: Long,
        @Schema(description = "상품 만족도 별점", example = "5")
        val rating: Int,
        @Schema(description = "상품 리뷰", example = "맛있어요~!")
        val content: String,
    )

    data class Response(
        @Schema(description = "생성된 리뷰 id", example = "1")
        val reviewId: Long
    )
}