package auth

import auth.auth.LogoutApi
import auth.auth.TokenExchangeApi
import auth.auth.TokenReissueApi
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Auth", description = "인증 API")
interface AuthApi : TokenReissueApi, LogoutApi, TokenExchangeApi