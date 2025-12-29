package member

import ApiResponse
import common.Scheme
import extension.applyHost
import extension.exchangeToApiResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.client.WebClient
import passport.Passport

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
                    .applyHost(host)
                    .path(MemberAddApi.PATH)
                    .build()
            }
            .bodyValue(request)
            .exchangeToApiResponse<MemberAddApi.Response>()
            .awaitSingle()
    }

    override suspend fun get(
        memberId: Long
    ): ApiResponse<MemberGetApi.Response> {
        return webClient.get()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(MemberGetApi.PATH)
                    .pathSegment(memberId.toString())
                    .build()
            }
            .exchangeToApiResponse<MemberGetApi.Response>()
            .awaitSingle()
    }

    override suspend fun updateNickname(
        request: UpdateNicknameApi.Request,
        passport: Passport
    ): ApiResponse<UpdateNicknameApi.Response> {
        return webClient.post()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(UpdateNicknameApi.PATH)
                    .build()
            }
            .bodyValue(request)
            .exchangeToApiResponse<UpdateNicknameApi.Response>()
            .awaitSingle()
    }

    override suspend fun nicknameExists(
        request: NicknameExistsApi.Request
    ): ApiResponse<NicknameExistsApi.Response> {
        return webClient.post()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(NicknameExistsApi.PATH)
                    .build()
            }
            .bodyValue(request)
            .exchangeToApiResponse<NicknameExistsApi.Response>()
            .awaitSingle()
    }

    override suspend fun getList(
        memberIds: List<Long>
    ): ApiResponse<MemberListApi.Response> {
        return webClient.post()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(MemberListApi.PATH)
                    .queryParam("memberIds", memberIds)
                    .build()
            }
            .exchangeToApiResponse<MemberListApi.Response>()
            .awaitSingle()
    }

    override suspend fun me(
        passport: Passport
    ): ApiResponse<MemberMeApi.Response> {
        return webClient.post()
            .uri { builder ->
                builder
                    .scheme(Scheme.HTTP)
                    .applyHost(host)
                    .path(MemberMeApi.PATH)
                    .build()
            }
            .exchangeToApiResponse<MemberMeApi.Response>()
            .awaitSingle()
    }
}
