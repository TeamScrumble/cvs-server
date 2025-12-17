package passport

data class Passport(
    val authId: Long,
    val authProvider: String,

    val memberId: Long,
    val email: String,
    val roles: Set<MemberRole>,
    val nickname: String,
)