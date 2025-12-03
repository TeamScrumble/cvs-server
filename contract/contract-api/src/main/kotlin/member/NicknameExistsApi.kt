package member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import passport.Passport

interface NicknameExistsApi {
    companion object {
        const val PATH = "/api/member/nickname-exists"
    }

    @Documented(
        summary = "낙네임 중복 확인 API",
        description = "닉네임 중복 확인 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun nicknameExists(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "닉네임", example = "John Doe")
        val nickname: String,
    )

    data class Response(
        @Schema(description = "존재 여부 (false 이면 변경 가능)", example = "false")
        val exists: Boolean,
    )
}
