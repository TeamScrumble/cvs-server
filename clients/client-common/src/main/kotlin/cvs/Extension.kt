package cvs

import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

inline fun <reified T : Any> WebClient.ResponseSpec.onError(
    crossinline action: (T) -> Throwable
): WebClient.ResponseSpec {
    return this
        .onClientError(action)
        .onServerError(action)
}

inline fun <reified T : Any> WebClient.ResponseSpec.onClientError(
    crossinline action: (T) -> Throwable
): WebClient.ResponseSpec {
    return onStatus({ it.is4xxClientError }) { handleErrorResponse(it, action) }
}

inline fun <reified T : Any> WebClient.ResponseSpec.onServerError(
    crossinline action: (T) -> Throwable
): WebClient.ResponseSpec {
    return onStatus({ it.is5xxServerError }) { handleErrorResponse(it, action) }
}

@PublishedApi
internal inline fun <reified T : Any> handleErrorResponse(
    response: ClientResponse,
    crossinline action: (T) -> Throwable
): Mono<Throwable> {
    return response
        .bodyToMono(T::class.java)
        .flatMap { Mono.error(action(it)) }
}

inline fun WebClient.ResponseSpec.onError(
    crossinline action: (Any) -> Throwable
): WebClient.ResponseSpec = onError<Any>(action)

inline fun WebClient.ResponseSpec.onClientError(
    crossinline action: (Any) -> Throwable
): WebClient.ResponseSpec = onClientError<Any>(action)

inline fun WebClient.ResponseSpec.onServerError(
    crossinline action: (Any) -> Throwable
): WebClient.ResponseSpec = onServerError<Any>(action)
