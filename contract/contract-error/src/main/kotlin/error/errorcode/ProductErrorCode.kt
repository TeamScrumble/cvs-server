package error.errorcode

enum class ProductErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    P_001("P_001", "등록된 상품이 없습니다."),
}