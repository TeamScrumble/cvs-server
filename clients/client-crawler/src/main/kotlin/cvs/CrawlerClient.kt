package cvs

import cvs.config.CrawlerProperties
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class CrawlerClient internal constructor(
    private val props: CrawlerProperties,
    private val webClient: WebClient
) {
    private data class Request(
        val type: String
    )

    private data class Response(
        val data: String
    )

    suspend fun call(type: String): CrawlerResult {
        val response = webClient.post()
            .uri(props.uri)
            .bodyValue(Request(type = "type"))
            .header("secret", props.secret)
            .retrieve()
            .onClientError { IllegalArgumentException() }
            .onServerError { IllegalStateException() }
            .bodyToMono(Response::class.java)
            .awaitSingle()

        return CrawlerResult(data = response.data)
    }
}