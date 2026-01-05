package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface ProductEsSyncApi {
    companion object {
        const val PATH = "/api/product/internal/es/sync/manual"
    }

    @Documented(
        summary = "상품 RDB와 ES를 동기화하는 API",
        description = "Source of truth <=> ElasticSearch 동기화 API",
        response = Response::class,
    )
    suspend fun crawl(passport: Passport): ApiResponse<Response>

    data class Response(
        @Schema(description = "요청 성공 시 동기화를 담당하는 jobId 반환", example = "1")
        val jobId: Long
    )
}