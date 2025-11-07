package cvs.auth.oauth

import com.fasterxml.jackson.databind.BeanProperty.Std
import cvs.auth.user.SocialUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService()
    : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private val delegate = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = delegate.loadUser(userRequest)
        // 현재 로그인한 registrationId(yml의 registration 이름)
        val registrationId = userRequest.clientRegistration.registrationId
        // provider 원본 JSON이 Map 형태로 들어옴
        val attributes = oAuth2User.attributes
        val mapped = when (registrationId) {
            "google" -> mapGoogle(attributes)
            "naver" -> mapNaver(attributes)
            else -> error("Unsupported provider: $registrationId")
        }

        // 저장
        val saved = upsertUser(mapped)
        // 시큐리티에 넘길 표준 attribute 구성
        val principalAttributes = mapOf(
            "provider" to saved.provider,
            "providerId" to saved.providerId,
            "email" to saved.email,
            "name" to saved.name,
            "profileImage" to saved.profileImage
        )
        // 권한 부여
        val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        return DefaultOAuth2User(authorities, principalAttributes, "providerId")
    }

    private fun upsertUser(user: StdUser): SocialUser {
        // todo: db 저장, 갱신 추후 구현
        return SocialUser(
            provider = user.provider,
            providerId = user.providerId,
            email = user.email,
            name = user.name,
            profileImage = user.profileImage
        )
    }

    private fun mapGoogle(attr: Map<String, Any?>) = StdUser(
        provider = "google",
        providerId = attr["sub"]?.toString() ?: "",
        email = attr["email"]?.toString(),
        name = attr["name"]?.toString(),
        profileImage = attr["picture"]?.toString()
    )

    private fun mapNaver(attr: Map<String, Any?>): StdUser {
        val response = attr["response"] as Map<String, Any?>

        return StdUser(
            provider = "naver",
            providerId = response["id"]?.toString() ?: "",
            email = response["email"]?.toString(),
            name = response["name"]?.toString(),
            profileImage = response["profile_image"]?.toString()
        )
    }

    data class StdUser(
        val provider: String,
        val providerId: String,
        val email: String?,
        val name: String?,
        val profileImage: String?
    )

}