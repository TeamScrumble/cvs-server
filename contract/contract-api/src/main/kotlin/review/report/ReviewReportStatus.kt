package review.report

enum class ReviewReportStatus(
    val status: String
) {
    PENDING("접수됨"), // (기본)
    IN_REVIEW("검토 중"),
    RESOLVED("처리 완료"),
    REJECTED("반려(신고 사유 부적절 등)")
}