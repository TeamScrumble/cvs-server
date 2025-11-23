package auth.application

import auth.config.TokenProperties
import cache.CacheMemory
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import extension.getOrThrow
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
    suspend fun issue(memberId: Long): AuthTokens {
        return issueTokens(memberId)
    }

    suspend fun reissue(refreshToken: String): AuthTokens {
        val principal = tokenProvider.decodeToken(refreshToken)
            ?: throw BusinessException(AuthErrorCode.A_002)

        if (principal.type != TokenType.REFRESH) {
            throw BusinessException(AuthErrorCode.A_002)
        }

        val savedToken = cacheMemory.get<String>(refreshTokenCacheKey(principal.memberId))
            ?: throw BusinessException(AuthErrorCode.A_002)
        if (refreshToken != savedToken) {
            throw BusinessException(AuthErrorCode.A_002)
        }

        return issueTokens(principal.memberId)
    }

    private suspend fun issueTokens(memberId: Long): AuthTokens {
        val memberResponse = memberApi.get(memberId).getOrThrow()

        val accessToken = tokenProvider.encodeToken(
            AuthPrincipal.accessToken(memberId, memberResponse.roles),
            tokenProperties.accessTokenExpires
        )
        val refreshToken = tokenProvider.encodeToken(
            AuthPrincipal.refreshToken(memberId),
            tokenProperties.refreshTokenExpires
        )

        cacheMemory.set(
            key = refreshTokenCacheKey(memberResponse.memberId),
            value = refreshToken,
            ttlMillis = tokenProperties.refreshTokenExpires
        )

        return AuthTokens(accessToken, refreshToken)
    }

    private fun refreshTokenCacheKey(memberId: Long): String {
        return REFRESH_TOKEN_CACHE_KEY_PREFIX + memberId
    }

    companion object {
        const val REFRESH_TOKEN_CACHE_KEY_PREFIX = "RefreshToken:"
    }
}