package error.errorcode

enum class MemberErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    M_001("M_001", "등록된 회원이 없습니다.")
}