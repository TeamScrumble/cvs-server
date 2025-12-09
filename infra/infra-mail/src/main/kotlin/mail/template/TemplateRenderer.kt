package auth.infra.mail.template

import org.springframework.stereotype.Component
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.context.Context

@Component
class TemplateRenderer(
    private val templateEngine: SpringTemplateEngine,
) {

    fun render(path: String, args: Map<String, Any>): String {
        val templateContext = Context().apply {
            args.forEach { (key, value) ->
                setVariable(key, value)
            }
        }

        return templateEngine.process(path, templateContext)
    }
}