package auth.user

data class SocialUser(
    val id: Long? = null,
    val provider: String,     // google | kakao | naver
    val providerId: String,   // 각 플랫폼 고유 ID
    val email: String?,
    val name: String?,
    val profileImage: String?
)