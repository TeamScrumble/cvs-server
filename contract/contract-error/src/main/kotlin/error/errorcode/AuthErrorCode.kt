package error.errorcode

enum class AuthErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    A_001("A_001", "잘못된 인증 토큰 입니다."),
    A_002("A_002", "잘못된 갱신 토큰 입니다."),

    A_003("A_003", "이메일 인증 메일 전송에 실패하였습니다."),
    A_004("A_004", "올바른 이메일 형식이 아님"),

    A_005("A_005", "인증코드 메일을 먼저 발송해주세요."),
    A_006("A_006", "인증코드가 올바르지 않습니다."),
    A_007("A_007", "올바르지 않은 인증 코드 형식입니다."),
}