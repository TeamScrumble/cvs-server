package auth.infra.mail

import auth.infra.mail.config.MailProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Component
class MailSender(
    private val mailWebClient: WebClient,
    private val props: MailProperties
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun send(
        to: String,
        subject: String,
        body: String,
        isHtml: Boolean = false
    ): Boolean {
        return try {
            mailWebClient.post()
                .uri("https://api.mailgun.net/v3/${props.domain}/messages")
                .headers {
                    it.setBasicAuth("api", props.apiKey)
                }
                .body(
                    BodyInserters.fromFormData("from", props.from)
                        .with("to", to)
                        .with("subject", subject)
                        .apply {
                            if (isHtml) {
                                with("html", body)
                            } else {
                                with("text", body)
                            }
                        }
                )
                .retrieve()
                .awaitBodyOrNull<String>()
                ?.let { true }
                ?: let {
                    logger.error("Could not send mail: ${to}")
                    false
                }
        } catch (e: Exception) {
            logger.error("Could not send mail: ${to}", e)
            false
        }
    }
}