package error.errorcode

enum class AuthErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    A_001("A_001", "잘못된 인증 토큰 입니다."),
    A_002("A_002", "잘못된 갱신 토큰 입니다."),

    A_003("A_003", "이메일 인증 메일 전송에 실패하였습니다."),
    A_004("A_004", "올바른 이메일 형식이 아님"),
}