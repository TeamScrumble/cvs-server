package auth.infra.oauth

import auth.application.AuthProviderKey
import auth.application.AuthService
import auth.domain.auth.AuthProvider
import kotlinx.coroutines.reactor.mono
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Suppress("UNCHECKED_CAST")
class OAuth2UserService (
    private val authService: AuthService
): ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private val delegate = DefaultReactiveOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        return delegate.loadUser(userRequest)
            .flatMap { oAuth2User ->
                val registrationId = userRequest.clientRegistration.registrationId
                val attributes = oAuth2User.attributes

                val provider = AuthProvider.from(registrationId)

                val oauthAttributes = when (provider) {
                    AuthProvider.GOOGLE -> fromGoogle(attributes)
                    AuthProvider.NAVER -> fromNaver(attributes)
                    AuthProvider.KAKAO -> fromKakao(attributes)
                    else -> {
                        return@flatMap Mono.error<OAuth2User>(
                            IllegalArgumentException("Not Oauth provider")
                        )
                    }
                }

                mono {
                    val authProviderKey = AuthProviderKey(provider, oauthAttributes.providerId)
                    authService.findOrInsertByProvider(authProviderKey, oauthAttributes.email)
                }.map { authMember ->
                    Oauth2AuthenticatedUser(
                        authId = authMember.authId,
                        provider = authMember.provider,
                        providerId = authMember.providerId,
                        memberId = authMember.memberId,
                        roles = authMember.roles,
                        email = authMember.email,
                        nickname = authMember.nickname
                    )
                }
            }
    }

    private fun fromGoogle(attr: Map<String, Any?>) = OauthAttributes(
        providerId = attr["sub"]?.toString()
            ?: throw kotlin.IllegalArgumentException("Invalid naver login response format"),
        email = attr["email"]?.toString()
            ?: throw kotlin.IllegalArgumentException("Invalid naver login response format"),
    )

    private fun fromNaver(attr: Map<String, Any?>): OauthAttributes {
        val response = attr["response"] as? Map<String, Any?>
            ?: throw kotlin.IllegalArgumentException("Invalid naver login response format")

        return OauthAttributes(
            providerId = response["id"]?.toString() ?: "",
            email = response["email"].toString()
        )
    }

    private fun fromKakao(attr: Map<String, Any?>): OauthAttributes {
        val kakaoAccount = attr["kakao_account"] as? Map<String, Any?>
            ?: throw kotlin.IllegalArgumentException("Invalid kakao login response format")

        return OauthAttributes(
            providerId = attr["id"]?.toString() ?: "",
            email = kakaoAccount["email"].toString()
        )
    }

    private data class OauthAttributes(
        val providerId: String,
        val email: String
    )
}
