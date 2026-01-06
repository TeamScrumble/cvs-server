package review

object ReviewPaths {
    private const val REVIEW_BASE = "/api/product/review"
    const val PRODUCT_BASE = "/api/product/{productId}/review"

    const val REVIEW_SUMMARY = "$PRODUCT_BASE/summary"

    const val REVIEW = "$REVIEW_BASE/{reviewId}"
    const val LIKE = "$REVIEW_BASE/{reviewId}/like"
    const val REPORT = "$REVIEW_BASE/{reviewId}/report"

    const val ASPECT_INFO = "$REVIEW_BASE/aspectInfo"
    const val REPORT_REASONS = "$REVIEW_BASE/report/reason"
}