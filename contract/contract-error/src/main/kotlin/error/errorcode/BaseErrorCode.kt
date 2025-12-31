package error.errorcode

enum class BaseErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    E_000("E_000", "알 수 없는 에러가 발생했습니다."),
    E_001("E_001", "잘못된 요청 형식 입니다."),
    E_002("E_002", "필수 요청 파라미터가 누락되었습니다."),
    E_003("E_003", "존재하지 않는 요청입니다."),
}