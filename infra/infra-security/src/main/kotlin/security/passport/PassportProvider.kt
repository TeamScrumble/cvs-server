package security.passport

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import passport.Passport
import java.util.Base64

@Component
class PassportProvider(
    private val objectMapper: ObjectMapper
) {

    fun encodePassport(passport: Passport): String {
        val json = objectMapper.writeValueAsString(passport)
        return Base64.getEncoder().encodeToString(json.toByteArray())
    }

    fun decodePassport(encoded: String): Passport {
        val json = Base64.getDecoder().decode(encoded)
        return objectMapper.readValue(json, Passport::class.java)
    }
}