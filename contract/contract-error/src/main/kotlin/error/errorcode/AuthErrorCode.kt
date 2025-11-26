package error.errorcode

enum class AuthErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    A_001("A_001", "잘못된 인증 토큰 입니다."),
    A_002("A_002", "잘못된 갱신 토큰 입니다."),
}