package member.member

import ApiResponse
import docs.Documented
import io.swagger.v3.oas.annotations.media.Schema
import member.member.field.Email

interface MemberAddApi {
    companion object {
        const val PATH = "/api/member"
    }

    @Documented(
        summary = "멤버 생성 API",
        description = "멤버를 생성하는 API",
        request = Request::class,
        response = Response::class,
    )
    suspend fun add(request: Request): ApiResponse<Response>

    data class Request(
        @Schema(description = "사용자 이메일", example = "mobility42@gmail.com")
        @field:Email
        val email: String
    )

    data class Response(
        @Schema(description = "생성된 회원 id", example = "1")
        val memberId: Long
    )
}
