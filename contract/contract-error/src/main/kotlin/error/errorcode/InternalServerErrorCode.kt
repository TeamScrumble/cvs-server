package error.errorcode

object InternalServerErrorCode : ErrorCode {
    override val code: String = "I_000"
    override val description: String = "알 수 없는 에러가 발생했습니다."
}