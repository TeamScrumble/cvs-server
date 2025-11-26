package auth

import ApiResponse
import passport.Passport

interface LogoutApi {
    companion object {
        const val PATH = "/api/auth/logout"
    }

    suspend fun logout(passport: Passport): ApiResponse<Response>

    data class Response(
        val success: Boolean,
    )
}