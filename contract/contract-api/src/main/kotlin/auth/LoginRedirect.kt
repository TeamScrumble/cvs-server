package auth

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object LoginRedirect {
    const val DEEPLINK_URL = "pyunpyun://auth/login/redirect"

    fun withQuery(vararg queryParams: Pair<String, String>): String {
        val queryString = queryParams.joinToString("&") { (k, v) -> "$k=${encode(v)}" }
        return "$DEEPLINK_URL?$queryString"
    }

    fun withTicket(ticket: String) = withQuery("ticket" to ticket)

    fun springRedirectWithTicket(ticket: String): String =
        "redirect:${withTicket(ticket)}"

    private fun encode(v: String): String =
        URLEncoder.encode(v, StandardCharsets.UTF_8)
}