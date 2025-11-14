package auth.controller

import auth.jwt.JwtService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class ReissueController(
    private val jwtService: JwtService
) {

    @PostMapping("/auth/reissue")
    fun reissue(@RequestHeader("Refresh") refreshToken: String): Map<String, String>? {
        val tokens = jwtService.reissue(refreshToken) ?: return null

        return mapOf("access" to tokens.first, "refresh" to tokens.second)
    }

}