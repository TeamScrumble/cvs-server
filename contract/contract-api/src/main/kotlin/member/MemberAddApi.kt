package member

import ApiResponse

interface MemberAddApi {
    companion object {
        const val PATH = "/api/member"
    }

    suspend fun add(request: Request): ApiResponse<Response>

    data class Request(
        val nickname: String
    )

    data class Response(
        val memberId: Long
    )
}
