package error.exception

import error.errorcode.ErrorCode

class BusinessException(
    override val errorCode: ErrorCode,
    val logMessage: String = errorCode.description
) : BaseException()