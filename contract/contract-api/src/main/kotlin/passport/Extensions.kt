package passport

val Passport.isAdmin: Boolean
    get() = MemberRole.ROLE_ADMIN in roles