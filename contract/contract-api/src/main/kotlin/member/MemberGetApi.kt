package member

import ApiResponse

interface MemberGetApi {
    companion object {
        const val PATH = "/api/member"
    }

    suspend fun get(memberId: Long): ApiResponse<Response>

    data class Response(
        val memberId: Long,
        val email: String,
        val roles: Set<String>,
        val nickname: String,
    )
}