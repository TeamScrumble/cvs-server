package member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface UpdateProfileImageApi {
    companion object {
        const val PATH = "/api/member/profile-image"
    }

    @Documented(
        summary = "프로필 이미지 변경 API",
        description = "프로필 이미지 변경 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun updateProfileImage(request: Request, passport: Passport): ApiResponse<Response>

    data class Request(
        @Schema(description = "변경할 프로필 이미지 url", example = "https://imgaeUrl.com")
        val profileImageUrl: String,
    )

    data class Response(
        @Schema(description = "수정된 회원 id", example = "1")
        val memberId: Long,
        @Schema(description = "변경된 프로플 이미지 url", example = "https://imgaeUrl.com")
        val profileImageUrl: String,
    )
}