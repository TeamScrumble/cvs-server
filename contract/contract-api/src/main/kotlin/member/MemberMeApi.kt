package member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface MemberMeApi {
    companion object {
        const val PATH = "/api/member/me"
    }

    @Documented(
        summary = "내 정보 조회 API",
        description = "accessToken으로 내 정보를 조회하는 APi",
        response = Response::class,
    )
    suspend fun me(passport: Passport): ApiResponse<Response>

    data class Response(
        @Schema(description = "사용자 id", example = "1")
        val memberId: Long,
        @Schema(description = "사용자 이메일", example = "mobility42@gmail.com")
        val email: String,
        @Schema(description = "사용자 닉네임", example = "사악한 펭귄")
        val nickname: String,
        @Schema(description = "사용자 프로필 이미지", example = "https://i.imgur.com/CHUednA_d.png")
        val profileImage: String,
    )
}