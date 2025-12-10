package gateway.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class ApiDocumentService(
    private val webClientBuilder: WebClient.Builder,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(ApiDocumentService::class.java)

    private val services = listOf(
        ServiceInfo(
            name = "Auth Service",
            url = "lb://auth-service/api/auth/oas.json",
        ),
        ServiceInfo(
            name = "Member Service",
            url = "lb://member-service/api/member/oas.json",
        ),
        ServiceInfo(
            name = "Product Service",
            url = "lb://product-service/api/product/oas.json",
        ),
        ServiceInfo(
            name = "Crawler Service",
            url = "lb://crawler-service/api/product/oas.json",
        ),
    )

    private val specCache: MutableMap<String, Map<String, Any?>> = ConcurrentHashMap()

    suspend fun generateOasJson(publicOnly: Boolean): Map<String, Any?> = coroutineScope {
        val results: List<ServiceResult> = services.map {
            async { fetchServiceResult(it) }
        }.awaitAll()

        val specs = results.mapNotNull { it.spec }
        if (specs.isEmpty()) {
            logger.warn("모든 OAS 스펙 조회 실패. 빈 스펙을 반환합니다.")
            return@coroutineScope emptyMap()
        }

        val merged = mergeSpecs(specs, publicOnly)

        merged["openapi"] = merged["openapi"] ?: "3.0.1"

        val baseInfo = merged["info"] as? Map<*, *> ?: emptyMap<String, Any?>()
        val version = baseInfo["version"] ?: "v0"

        val statusDescription = buildDescription(results, publicOnly)

        merged["info"] = mapOf(
            "title" to "Pyunpyun Api Documents",
            "version" to version,
            "description" to statusDescription,
        )

        merged["servers"] = listOf(
            mapOf(
                "url" to "https://dev-api.pyunpyun.com",
                "description" to "Gateway"
            )
        )

        return@coroutineScope merged
    }

    private suspend fun fetchServiceResult(config: ServiceInfo): ServiceResult {
        val webClient = webClientBuilder.build()
        val cacheKey = config.name

        return try {
            val json = webClient.get()
                .uri(config.url)
                .retrieve()
                .bodyToMono(String::class.java)
                .timeout(Duration.ofMillis(500))
                .awaitSingle()

            val spec: Map<String, Any?> = objectMapper.readValue(json)

            specCache[cacheKey] = spec

            ServiceResult(
                info = config,
                spec = spec,
                alive = true,
                fromCache = false,
            )
        } catch (e: Exception) {
            logger.warn("OAS 스펙 조회 실패: name=${config.name}, url=${config.url}, message=${e.message}")

            val cached = specCache[cacheKey]

            ServiceResult(
                info = config,
                spec = cached,
                alive = false,
                fromCache = cached != null,
            )
        }
    }

    private fun buildDescription(
        results: List<ServiceResult>,
        publicOnly: Boolean
    ): String {
        return buildString {
            if (!publicOnly) {
                appendLine("**Microservices**<br/>")
                results.forEachIndexed { idx, result ->
                    val (emoji, statusText) = when {
                        result.alive -> "🟢" to "UP"
                        result.spec != null -> "🟡" to "CACHED"
                        else -> "🔴" to "DOWN"
                    }

                    if (idx == results.size - 1) {
                        appendLine("$emoji ${result.info.name} ($statusText)")
                    } else {
                        appendLine("$emoji ${result.info.name} ($statusText)<br/>")
                    }
                }
                appendLine()
            }

            appendLine("**Social Logins** <br/>")
            appendLine("☑️ kakao : <a href=\"https://dev-api.pyunpyun.com/oauth2/authorization/kakao\">https://dev-api.pyunpyun.com/oauth2/authorization/kakao</a><br/>")
            appendLine("☑️ google : <a href=\"https://dev-api.pyunpyun.com/oauth2/authorization/google\">https://dev-api.pyunpyun.com/oauth2/authorization/google</a><br/>")
            appendLine("☑️ naver : <a href=\"https://dev-api.pyunpyun.com/oauth2/authorization/naver\">https://dev-api.pyunpyun.com/oauth2/authorization/naver</a><br/>")
        }
    }

    private fun mergeSpecs(
        specs: List<Map<String, Any?>>,
        publicOnly: Boolean
    ): MutableMap<String, Any?> {
        if (specs.isEmpty()) return mutableMapOf()

        val base = specs.first().toMutableMap()

        val mergedPaths = mutableMapOf<String, Any?>()
        val mergedSchemas = mutableMapOf<String, Any?>()
        val mergedResponses = mutableMapOf<String, Any?>()

        specs.forEach { spec ->
            val paths = spec["paths"] as? Map<String, Any?> ?: emptyMap()

            val filteredPaths = if (publicOnly) {
                paths.filterKeys { path -> !path.contains("internal") }
            } else {
                paths
            }

            mergedPaths.putAll(filteredPaths)

            val components = spec["components"] as? Map<String, Any?> ?: emptyMap()
            val schemas = components["schemas"] as? Map<String, Any?> ?: emptyMap()
            val responses = components["responses"] as? Map<String, Any?> ?: emptyMap()

            mergedSchemas.putAll(schemas)
            mergedResponses.putAll(responses)
        }

        base["paths"] = mergedPaths

        val baseComponents =
            (base["components"] as? Map<String, Any?> ?: emptyMap()).toMutableMap()
        baseComponents["schemas"] = mergedSchemas
        baseComponents["responses"] = mergedResponses
        base["components"] = baseComponents

        return base
    }
}