package auth.application

import auth.config.TokenProperties
import cache.CacheMemory
import member.MemberApi
import org.springframework.stereotype.Service
import security.token.AuthPrincipal
import security.token.TokenProvider
import security.token.TokenType

@Service
class TokenService(
    private val tokenProvider: TokenProvider,
    private val cacheMemory: CacheMemory,
    private val memberApi: MemberApi,
    private val tokenProperties: TokenProperties
) {
    suspend fun reissue(refreshToken: String): AuthTokens {
        val principal = tokenProvider.decodeToken(refreshToken)
            ?: throw IllegalArgumentException("잘못된 갱신 토큰 입니다.")

        if (principal.type != TokenType.REFRESH) {
            throw IllegalArgumentException("갱신 토큰이 아닙니다.")
        }

        val savedToken = cacheMemory.get<String>(refreshTokenCacheKey(principal.memberId))
            ?: throw IllegalArgumentException("존재하지 않는 갱신 토큰 입니다.")
        if (refreshToken != savedToken) {
            throw IllegalArgumentException("갱신 토큰이 일치하지 않습니다.")
        }

        val memberResponse = memberApi.get(principal.memberId)

        if (memberResponse.status == 404) {
            throw IllegalArgumentException("존재하지 않는 회원입니다.")
        }
        val member = memberResponse.body

        val tokens = createTokens(member.memberId, member.roles)

        cacheMemory.set(
            key = refreshTokenCacheKey(member.memberId),
            value = tokens.refreshToken,
            ttlMillis = tokenProperties.refreshTokenExpires
        )

        return tokens
    }

    private fun createTokens(memberId: Long, roles: Set<String>): AuthTokens {
        val accessToken = tokenProvider.encodeToken(
            AuthPrincipal.accessToken(memberId, roles),
            tokenProperties.accessTokenExpires
        )
        val refreshToken = tokenProvider.encodeToken(
            AuthPrincipal.refreshToken(memberId),
            tokenProperties.refreshTokenExpires
        )
        return AuthTokens(accessToken, refreshToken)
    }

    private fun refreshTokenCacheKey(memberId: Long): String {
        return REFRESH_TOKEN_CACHE_KEY_PREFIX + memberId
    }

    companion object {
        const val REFRESH_TOKEN_CACHE_KEY_PREFIX = "refresh_token:"
    }
}