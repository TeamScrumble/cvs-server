package auth.auth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

interface TokenReissueApi {
    companion object {
        const val PATH = "/api/auth/token/reissue"
    }

    @Documented(
        summary = "토큰 재발행 API",
        description = "Refresh Token을 기반으로 인증/갱신 토큰을 갱신하는 API",
        response = Response::class,
    )
    suspend fun reissue(
        @Parameter(
            description = "RefreshToken 을 담은 인증 헤더",
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        refreshHeader: String
    ): ApiResponse<Response>

    data class Response(
        @Schema(description = "인증 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val accessToken: String,
        @Schema(description = "갱신 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        val refreshToken: String
    )
}
