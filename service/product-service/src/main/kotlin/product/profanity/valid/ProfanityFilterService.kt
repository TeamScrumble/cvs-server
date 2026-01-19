package product.profanity.valid

import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import io.github.jwhyee.profanity.validator.ProfanityDetectedException
import io.github.jwhyee.profanity.validator.ProfanityValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import product.common.valid.dto.ProfanityResult

@Service
class ProfanityFilterService(
    private val profanityValidator: ProfanityValidator
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun check(keyword: String): ProfanityResult = runCatching {
        profanityValidator.validate(keyword)
        ProfanityResult()
    }.getOrElse { e ->
        if (e is ProfanityDetectedException) {
            ProfanityResult(true, e.detected)
        } else {
            logger.warn("[ProfanityValidator] exception = {}", e.message)
            throw BusinessException(ProductErrorCode.P_012)
        }
    }
}