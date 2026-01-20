package validation

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface ProfanityGetApi {
    companion object {
        const val PATH = "/api/profanity/internal"
    }

    @Documented(
        summary = "쿼리 파라미터로 동작하는 비속어 조회 API",
        description = "특정 문자열을 대상으로 비속어가 포함되어 있는지 확인하는 API",
        response = Response::class,
    )
    suspend fun get(keyword: String): ApiResponse<Response>

    data class Response(
        @Schema(description = "비속어 포함 여부", example = "false")
        val hasBadWord: Boolean,
        @Schema(description = "포함된 비속어 목록", example = "시@")
        val badWords: List<String>
    )
}