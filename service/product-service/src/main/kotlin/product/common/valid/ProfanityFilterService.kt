package product.common.valid

import io.github.jwhyee.profanity.validator.ProfanityDetectedException
import io.github.jwhyee.profanity.validator.ProfanityValidator
import org.springframework.stereotype.Service
import product.common.valid.dto.ProfanityResult

@Service
class ProfanityFilterService(
    private val profanityValidator: ProfanityValidator
) {
    fun check(keyword: String): ProfanityResult = runCatching {
        profanityValidator.validate(keyword)
        ProfanityResult()
    }.getOrElse { e ->
        if (e is ProfanityDetectedException) {
            ProfanityResult(true, e.detected)
        } else {
            throw e
        }
    }
}