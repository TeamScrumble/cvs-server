package product.product

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ProductPopularSearchApi {
    companion object {
        const val PATH = "/api/product/popular-search"
    }

    @Documented(
        summary = "인기 검색어 조회 API",
        description = "현재 시간을 기준으로 최근 1시간 동안 가장 많이 검색된 상품명을 조회하는 API. 1시간 전 데이터와 비교하여 순위 변화를 표시합니다.",
        response = Response::class,
    )
    suspend fun getPopularSearches(): ApiResponse<Response>

    data class Response(
        @Schema(description = "인기 검색어 목록 (1등부터 10등까지)")
        val popularSearches: List<PopularSearchItem>
    )

    data class PopularSearchItem(
        @Schema(description = "순위 (1~10)")
        val rank: Int,

        @Schema(description = "상품명")
        val productTitle: String,

        @Schema(description = "검색 횟수")
        val searchCount: Long,

        @Schema(description = "순위 변화 상태", example = "NEW/UP/DOWN/SAME")
        val changeStatus: ChangeStatus,

        @Schema(description = "이전 순위 (NEW인 경우 null)", example = "3")
        val previousRank: Int?,

        @Schema(description = "이전 검색 횟수 (NEW인 경우 null)", example = "15")
        val previousSearchCount: Long?
    )

    enum class ChangeStatus {
        @Schema(description = "새로 인기 검색어에 올라옴")
        NEW,

        @Schema(description = "검색 횟수 증가 또는 순위 상승")
        UP,

        @Schema(description = "검색 횟수 감소 또는 순위 하락")
        DOWN,

        @Schema(description = "변화 없음")
        SAME
    }
}

