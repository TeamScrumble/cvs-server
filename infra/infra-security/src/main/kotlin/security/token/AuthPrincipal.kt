package security.token

data class AuthPrincipal(
    val memberId: Long,
    val roles: Set<String>
)
