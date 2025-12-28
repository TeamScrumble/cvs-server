package product.review.application.report

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import review.report.ReviewReportAddApi
import review.report.ReviewReportReasonCode
import review.report.ReviewReportReasonGetApi

@Service
class ReviewReportReasonService() {

    fun getReasons(): List<ReviewReportReasonGetApi.Response> =
        ReviewReportReasonCode.list()

    fun validateReasonAndContent(
        request: ReviewReportAddApi.Request
    ): Validated {
        val reason = ReviewReportReasonCode.from(request.reasonCode)
        val content = request.content.trim()

        if (content.length < 10 || content.length > 500) {
            throw BusinessException(ReviewErrorCode.R_012)
        }

        return Validated(
            reasonCode = reason.code,
            content = content
        )
    }

    data class Validated(
        val reasonCode: String,
        val content: String
    )
}