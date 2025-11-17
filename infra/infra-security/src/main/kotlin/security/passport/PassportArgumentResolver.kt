package security.passport

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import passport.Passport
import reactor.core.publisher.Mono
import java.util.*

@Component
class PassportArgumentResolver(
    private val objectMapper: ObjectMapper
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasPassportAnnotation = parameter.hasParameterAnnotation(RequestPassport::class.java)
        val isPassportType = Passport::class.java.isAssignableFrom(parameter.parameterType)

        return hasPassportAnnotation && isPassportType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<in Any> {
        val encodedPassport = exchange.request.headers.getFirst(PassportHeader.KEY)
            ?: return Mono.error(IllegalStateException("no passport found"))
        val passportJson = Base64.getDecoder().decode(encodedPassport)
        return Mono.just(objectMapper.readValue(passportJson, Passport::class.java))
    }
}
