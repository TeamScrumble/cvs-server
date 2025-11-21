package auth

import ApiResponse

interface TokenReissueApi {
    companion object {
        const val PATH = "/api/auth/token/reissue"
    }

    suspend fun reissue(refreshHeader: String): ApiResponse<Response>

    data class Response(
        val accessToken: String,
        val refreshToken: String
    )
}
