package product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ProductLikeApi {
    companion object {
        const val PATH = "/api/product/like"
    }

    @Documented(
        summary = "상품 좋아요 등록 및 취소 API",
        description = "상품에 대한 좋아요 및 좋아요 취소를 누르는 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun toggle(passport: Passport, request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "상품 고유 번호", example = "1")
        val productId: Long
    )

    data class Response(
        @Schema(description = "해당 회원에 대한 좋아요 여부", example = "true")
        val liked: Boolean,

        @Schema(description = "상품 좋아요 개수", example = "42")
        val likeCount: Int
    )
}