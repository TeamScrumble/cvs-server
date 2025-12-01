package gateway.docs

import error.errorcode.ErrorCode
import org.springframework.stereotype.Component

@Component
class ErrorDocumentService {

    fun generateErrorJson(): Map<String, List<Map<String, String>>> {
        val errorCodes = ErrorCode.allErrorCodes()
        return errorCodes.groupBy({ it::class.simpleName ?: "BaseErrorCode" }) { errorCode ->
            mapOf(
                "code" to errorCode.code,
                "description" to errorCode.description
            )
        }
    }
}