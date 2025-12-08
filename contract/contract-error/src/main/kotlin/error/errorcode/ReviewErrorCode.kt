package error.errorcode

enum class ReviewErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    R_001("R_001", "존재하지 않는 리뷰입니다."),
    R_002("R_002", "만족도는 1~5 범위의 값만 입력할 수 있습니다."),
    R_003("R_003", "리뷰는 10~500자 길이로 작성해야 합니다."),
}