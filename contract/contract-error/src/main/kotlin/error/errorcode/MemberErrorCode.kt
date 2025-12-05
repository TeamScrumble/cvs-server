package error.errorcode

enum class MemberErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    M_001("M_001", "등록된 회원이 없습니다."),
    M_002("M_002", "올바르지 않은 닉네임 입니다."),
    M_003("M_003", "이미 사용중인 닉네임 입니다."),
}