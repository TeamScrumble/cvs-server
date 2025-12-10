package auth.infra.mail

sealed class MailContent{

    abstract val to: String
    abstract val subject: String

    data class Text(
        override val to: String,
        override val subject: String,
        val text: String
    ) : MailContent()

    data class Html(
        override val to: String,
        override val subject: String,
        val text: String
    ) : MailContent()

    data class Template private constructor(
        override val to: String,
        override val subject: String,
        val path: String,
        val args: Map<String, Any>
    ) : MailContent() {

        constructor(
            to: String,
            subject: String,
            template: String,
            vararg args: Pair<String, Any>
        ) : this(
            to = to,
            subject = subject,
            path = template,
            args = mapOf(*args)
        )
    }
}