package member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema

interface MemberGetApi {
    companion object {
        const val PATH = "/api/member/internal"
    }

    @Documented(
        summary = "회원 조회 API",
        description = "회원 Id로 회원 정보를 조회하는 API",
        response = Response::class,
    )
    suspend fun get(memberId: Long): ApiResponse<Response>

    data class Response(
        @Schema(description = "사용자 id", example = "1")
        val memberId: Long,
        @Schema(description = "사용자 이메일", example = "mobility42@gmail.com")
        val email: String,
        @Schema(description = "사용자 권한", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
        val roles: Set<String>,
        @Schema(description = "사용자 닉네임", example = "사악한 펭귄")
        val nickname: String,
        @Schema(description = "사용자 프로필 이미지", example = "https://i.imgur.com/CHUednA_d.png")
        val profileImage: String,
    )
}