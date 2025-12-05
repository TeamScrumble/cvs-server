package member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface UpdateNicknameApi {
    companion object {
        const val PATH = "/api/member/nickname"
    }

    @Documented(
        summary = "낙네임 변경 API",
        description = "닉네임 변경 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun updateNickname(request: Request, passport: Passport): ApiResponse<Response>

    data class Request(
        @Schema(description = "변경할 닉네임", example = "John Doe")
        val nickname: String,
    )

    data class Response(
        @Schema(description = "수정되 회원 id", example = "1")
        val memberId: Long,
        @Schema(description = "변경된 낙내암", example = "John Doe")
        val nickname: String,
    )
}
