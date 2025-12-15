package auth.support

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Component
import security.token.AuthPrincipal
import security.token.TokenProvider

@Component
class LocalTokenGenerator(
    private val environment: Environment,
    private val tokenProvider: TokenProvider,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 로컬 환경에서만 동작합니다.
     * 아래와 같이 실행 시 변수를 주입하면 토큰을 생성해 줍니다.
     * --memberId=1 --roles=ROLE_USER,ROLE_ADMIN
     */
    override fun run(args: ApplicationArguments) {
        if (!environment.acceptsProfiles(Profiles.of(LOCAL_PROFILE))) {
            return
        }

        val memberId = args.getOptionValues("memberId")?.firstOrNull()?.toLongOrNull() ?: return
        val roles = args.getOptionValues("roles")
            ?.firstOrNull()
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: return

        val authPrincipal = AuthPrincipal.accessToken(memberId, roles)
        val token = tokenProvider.encodeToken(authPrincipal, TWELVE_HOURS)

        log.info("Local Access Token : {}", token)
    }

    companion object {
        const val LOCAL_PROFILE = "local"
        const val TWELVE_HOURS = 12 * 60 * 60 * 1000L
    }
}
