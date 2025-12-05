package auth.application

import auth.config.AuthServiceTokenProperties
import auth.infra.cache.RefreshTokenCacheMemory
import auth.infra.cache.TokenTicketCacheMemory
import error.errorcode.AuthErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import member.MemberApi
import org.springframework.stereotype.Service
import security.token.AuthPrincipal
import security.token.TokenProvider
import security.token.TokenType
import java.util.UUID

@Service
class TokenService(
    private val tokenProvider: TokenProvider,
    private val refreshTokenCacheMemory: RefreshTokenCacheMemory,
    private val tokenTicketCacheMemory: TokenTicketCacheMemory,
    private val memberApi: MemberApi,
    private val tokenProperties: AuthServiceTokenProperties
) {
    suspend fun issueTicket(memberId: Long): String {
        val tokens = issueTokens(memberId)
        val ticket = UUID.randomUUID().toString()
        tokenTicketCacheMemory.set(ticket, tokens)
        return ticket
    }

    suspend fun reissue(refreshToken: String): AuthTokens {
        val principal = tokenProvider.decodeToken(refreshToken)

        if (principal.type != TokenType.REFRESH) {
            throw BusinessException(AuthErrorCode.A_002)
        }

        val savedToken = refreshTokenCacheMemory.get(principal.memberId)
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

        refreshTokenCacheMemory.set(
            memberId = memberResponse.memberId,
            refreshToken = refreshToken,
        )

        return AuthTokens(accessToken, refreshToken)
    }
}