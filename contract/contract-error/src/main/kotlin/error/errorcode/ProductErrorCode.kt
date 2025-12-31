package error.errorcode

enum class ProductErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    P_001("P_001", "등록된 상품이 없습니다."),
    P_002("P_002", "상품 등록 권한이 없습니다."),

    P_003("P_003", "편의점 정보가 없습니다."),
    P_004("P_004", "검색어는 두 글자 이상 입력해 주세요.")
}