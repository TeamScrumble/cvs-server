package auth

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface LogoutApi {
    companion object {
        const val PATH = "/api/auth/logout"
    }
    @Documented(
        summary = "로그아웃 API",
        description = "Access Token을 기반으로 로그아웃 API (프론트에서도 인증 토큰을 지워줘야 함)",
        response = Response::class,
    )
    suspend fun logout(passport: Passport): ApiResponse<Response>

    data class Response(
        @Schema(description = "성공 여부", example = "true")
        val success: Boolean,
    )
}