package passport

data class Passport(
    val memberId: Long,
    val roles: Set<String>,
    val nickname: String,
)