package security.token

data class AuthPrincipal(
    val memberId: Long,
    val type: Enum<TokenType>,
    val roles: Set<String>? = null,
)
