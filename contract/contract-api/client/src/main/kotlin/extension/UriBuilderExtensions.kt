package extension

import org.springframework.web.util.UriBuilder

fun UriBuilder.applyHost(host: String): UriBuilder {
    return if (":" in host) {
        val (rawHost, rawPort) = host.split(":", limit = 2)
        this.host(rawHost).port(rawPort)
    } else {
        host(host)
    }
}
