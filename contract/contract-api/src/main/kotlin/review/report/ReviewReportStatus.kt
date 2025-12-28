package review.report

enum class ReviewReportStatus(
    val code: String,
    val status: String
) {
    PENDING("PENDING", "접수됨"), // (기본)
    IN_REVIEW("IN_REVIEW", "검토 중"),
    RESOLVED("RESOLVED", "처리 완료"),
    REJECTED("REJECTED", "반려(신고 사유 부적절 등)")
}