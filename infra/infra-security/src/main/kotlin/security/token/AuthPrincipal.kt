package security.token

data class AuthPrincipal(
    val memberId: Long,
    val type: TokenType,
    val roles: Set<String> = emptySet(),
) {
    companion object {
        fun accessToken(
            memberId: Long,
            roles: Set<String>
        ) = AuthPrincipal(
            memberId = memberId,
            type = TokenType.ACCESS,
            roles = roles
        )

        fun refreshToken(memberId: Long) = AuthPrincipal(
            memberId = memberId,
            type = TokenType.REFRESH
        )
    }
}
