package error.exception

import error.errorcode.ErrorCode
import java.lang.RuntimeException

abstract class BaseException: RuntimeException() {
    abstract val errorCode: ErrorCode
}
