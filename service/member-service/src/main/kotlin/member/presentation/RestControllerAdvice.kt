package member.presentation

import error.ErrorResponse
import error.errorcode.BaseErrorCode
import error.errorcode.ErrorCode
import error.exception.BusinessException
import error.exception.InternalServerException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.MissingRequestValueException
import ApiResponse

@RestControllerAdvice
class RestControllerAdvice {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        log.info(e.logMessage)
        return buildErrorResponse(e.errorCode, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InternalServerException::class)
    fun handleInternalServerException(e: InternalServerException): ResponseEntity<ApiResponse<Nothing>> {
        log.error(e.logMessage, e)
        return buildErrorResponse(e.errorCode, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        log.info(e.message)
        return buildErrorResponse(BaseErrorCode.E_001, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingRequestValueException::class)
    fun handleMissingRequestValueException(e: MissingRequestValueException): ResponseEntity<ApiResponse<Nothing>> {
        log.info(e.message)
        return buildErrorResponse(BaseErrorCode.E_002, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unexpected exception", e)
        return buildErrorResponse(BaseErrorCode.E_000, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun buildErrorResponse(
        errorCode: ErrorCode,
        status: HttpStatus,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val body = ApiResponse.Error<Nothing>(
            error = ErrorResponse(
                code = errorCode.code,
                description = errorCode.description
            ),
            status = status.value()
        )
        return ResponseEntity.status(status).body(body)
    }
}