package member

import ApiResponse
import common.Scheme
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient

class MemberApiClient(
    private val webClient: WebClient,
    private val host: String
) : MemberApi {

    override suspend fun add(
        request: MemberAddApi.Request
    ): ApiResponse<MemberAddApi.Response> {
        return webClient.post()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .host(host)
                    .path(MemberAddApi.PATH)
                    .build()
            }
            .bodyValue(request)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<ApiResponse<MemberAddApi.Response>>() {})
            .awaitSingle()
    }

    override suspend fun get(
        memberId: Long
    ): ApiResponse<MemberGetApi.Response> {
        return webClient.get()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .host(host)
                    .path(MemberGetApi.PATH)
                    .pathSegment(memberId.toString())
                    .build()
            }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<ApiResponse<MemberGetApi.Response>>() {})
            .awaitSingle()
    }
}
