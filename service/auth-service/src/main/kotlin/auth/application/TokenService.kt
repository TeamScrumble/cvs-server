package auth.application

import auth.config.AuthServiceTokenProperties
import auth.domain.auth.AuthRepository
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
    private val tokenProperties: AuthServiceTokenProperties,
) {
    suspend fun issueTicket(memberId: Long, roles: Set<String>): String {
        val tokens = issueTokens(memberId, roles)
        val ticket = UUID.randomUUID().toString()
        tokenTicketCacheMemory.set(ticket, tokens)
        return ticket
    }

    suspend fun decodeRefreshToken(refreshToken: String): AuthPrincipal {
        val principal = tokenProvider.decodeToken(refreshToken)

        if (principal.type != TokenType.REFRESH) {
            throw BusinessException(AuthErrorCode.A_002)
        }

        val savedToken = refreshTokenCacheMemory.get(principal.memberId)
            ?: throw BusinessException(AuthErrorCode.A_002)
        if (refreshToken != savedToken) {
            throw BusinessException(AuthErrorCode.A_002)
        }

        return principal
    }

    suspend fun issueTokens(memberId: Long, roles: Set<String>): AuthTokens {
        val accessToken = tokenProvider.encodeToken(
            AuthPrincipal.accessToken(memberId, roles),
            tokenProperties.accessTokenExpires
        )
        val refreshToken = tokenProvider.encodeToken(
            AuthPrincipal.refreshToken(memberId),
            tokenProperties.refreshTokenExpires
        )

        refreshTokenCacheMemory.set(
            memberId = memberId,
            refreshToken = refreshToken,
        )

        return AuthTokens(accessToken, refreshToken)
    }
}