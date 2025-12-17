package passport

enum class MemberRole {
    ROLE_USER, ROLE_ADMIN;

    companion object {
        fun Set<String>.toRoleSet() = map { MemberRole.valueOf(it) }.toSet()

        fun Set<MemberRole>.toStringSet() = map { it.name }.toSet()
    }
}