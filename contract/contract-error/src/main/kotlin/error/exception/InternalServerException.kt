package error.exception

import error.errorcode.ErrorCode
import error.errorcode.InternalServerErrorCode

class InternalServerException(
    val logMessage: String = InternalServerErrorCode.description,
) : BaseException() {
    override val errorCode: ErrorCode = InternalServerErrorCode
}
