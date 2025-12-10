package auth.domain.auth

enum class AuthProvider {
    GOOGLE, KAKAO, NAVER, INTERNAL;

    companion object {
        fun from(name: String): AuthProvider {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid provider name: $name")
        }
    }
}