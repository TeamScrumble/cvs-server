package extension

import ApiResponse
import error.errorcode.ErrorCode
import error.exception.InternalServerException

fun <T> ApiResponse<T>.get(): T? {
    return if (success) body else null
}

suspend fun <T> ApiResponse<T>.getOrThrow(
    exceptionMapper: suspend (errorCode: ErrorCode) -> Throwable
): T {
    return if (success) {
        requireBody()
    } else {
        val errorCode = requireErrorCode()
        throw exceptionMapper(errorCode)
    }
}

suspend fun <T> ApiResponse<T>.getOrElse(
    fallback: suspend (errorCode: ErrorCode) -> T
): T {
    return if (success) {
        requireBody()
    } else {
        val errorCode = requireErrorCode()
        fallback(errorCode)
    }
}

fun <T> ApiResponse<T>.getOrDefault(
    defaultValue: T
): T {
    return if (success) {
        requireBody()
    } else {
        defaultValue
    }
}

suspend fun <T> ApiResponse<T>.onError(
    block: suspend (errorCode: ErrorCode) -> Unit
): ApiResponse<T> {
    if (!success) {
        val errorCode = requireErrorCode()
        block(errorCode)
    }
    return this
}

suspend fun <T> ApiResponse<T>.onSuccess(
    block: suspend (body: T) -> Unit
): ApiResponse<T> {
    if (success) {
        val b = requireBody()
        block(b)
    }
    return this
}

private fun <T> ApiResponse<T>.requireBody(): T {
    if (!success) {
        throw InternalServerException("success=false 인데 requireBody() 를 호출했습니다. status=$status, error=$error")
    }
    return body ?: throw InternalServerException("success=true 이지만 body=null 입니다. status=$status")
}

private fun <T> ApiResponse<T>.requireErrorCode(): ErrorCode {
    if (success) {
        throw InternalServerException("success=true 인데 requireErrorCode() 를 호출했습니다. status=$status")
    }
    val err = error ?: throw InternalServerException("success=false 이지만 error=null 입니다. status=$status")
    return ErrorCode.from(err.code)
}
