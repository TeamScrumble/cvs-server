package extension

import ApiResponse
import error.errorcode.ErrorCode
import error.exception.InternalServerException

fun <T> ApiResponse<T>.getOrNull(): T? = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Error -> null
}

suspend fun <T> ApiResponse<T>.getOrThrow(
    exceptionMapper: suspend (errorCode: ErrorCode) -> Throwable
): T = when (this) {
    is ApiResponse.Success -> requireBody()
    is ApiResponse.Error -> {
        val errorCode = requireErrorCode()
        throw exceptionMapper(errorCode)
    }
}

suspend fun <T> ApiResponse<T>.getOrElse(
    fallback: suspend (errorCode: ErrorCode) -> T
): T = when (this) {
    is ApiResponse.Success -> requireBody()
    is ApiResponse.Error -> {
        val errorCode = requireErrorCode()
        fallback(errorCode)
    }
}

fun <T> ApiResponse<T>.getOrDefault(
    defaultValue: T
): T = when (this) {
    is ApiResponse.Success -> requireBody()
    is ApiResponse.Error -> defaultValue
}

suspend fun <T> ApiResponse<T>.onError(
    block: suspend (errorCode: ErrorCode) -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Error) {
        val errorCode = requireErrorCode()
        block(errorCode)
    }
    return this
}

suspend fun <T> ApiResponse<T>.onSuccess(
    block: suspend (body: T) -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        val b = requireBody()
        block(b)
    }
    return this
}

private fun <T> ApiResponse<T>.requireBody(): T = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Error -> throw InternalServerException(
        "Success 응답이 아닌데 requireBody() 를 호출했습니다. status=$status, error=$error"
    )
}

private fun <T> ApiResponse<T>.requireErrorCode(): ErrorCode = when (this) {
    is ApiResponse.Success -> throw InternalServerException(
        "Error 응답이 아닌데 requireErrorCode() 를 호출했습니다. status=$status"
    )
    is ApiResponse.Error -> ErrorCode.from(error.code)
}