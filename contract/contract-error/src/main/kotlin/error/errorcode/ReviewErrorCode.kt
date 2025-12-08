package error.errorcode

enum class ReviewErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    R_001("R_001", "존재하지 않는 리뷰입니다."),
    R_002("R_002", "만족도는 1~5 범위의 값만 입력할 수 있습니다."),
    R_003("R_003", "리뷰는 10~500자 길이로 작성해야 합니다."),

    R_004("R_004", "존재하지 않는 평가 항목입니다."),
    R_005("R_005", "존재하지 않는 평가 옵션입니다."),
    R_006("R_006", "해당 평가 항목에 해당하지 않는 평가 옵션입니다."),
}