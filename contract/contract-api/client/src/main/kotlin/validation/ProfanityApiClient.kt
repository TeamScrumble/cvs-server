package validation

import ApiResponse
import common.Scheme
import extension.applyHost
import extension.exchangeToApiResponse
import kotlinx.coroutines.reactor.awaitSingle
import member.MemberApi
import org.springframework.web.reactive.function.client.WebClient
import passport.Passport

class ProfanityApiClient(
    private val webClient: WebClient,
    private val host: String
) : ProfanityGetApi {

    override suspend fun get(
        keyword: String
    ): ApiResponse<ProfanityGetApi.Response> {
        return webClient.get()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(ProfanityGetApi.PATH)
                    .queryParam("keyword", keyword)
                    .build()
            }
            .exchangeToApiResponse<ProfanityGetApi.Response>()
            .awaitSingle()
    }
}
