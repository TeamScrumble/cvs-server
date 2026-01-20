package error.errorcode

enum class ProductErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    // 0~9 : 상품 상세 관련
    P_001("P_001", "등록된 상품이 없습니다."),
    P_002("P_002", "상품 등록 권한이 없습니다."),
    P_003("P_003", "편의점 정보가 없습니다."),

    // 10~19 : 상품 검색 관련
    P_010("P_010", "검색어를 입력해 주세요."),
    P_011("P_011", "잘못된 검색어입니다."),
    P_012("P_012", "검색에 실패했습니다. 다시 시도해주세요."),

    // 90~99 : 권한 관련
    P_090("P_090", "상품 동기화 권한이 없습니다."),
}