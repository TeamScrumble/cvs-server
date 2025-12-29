package product.review.application.report

import db.transactional.Transactional
import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import product.review.application.ReviewService
import review.report.ReviewReportAddApi
import review.report.ReviewReportReasonGetApi

@Service
class ReviewReportFacade(
    private val reviewService: ReviewService,
    private val reasonService: ReviewReportReasonService,
    private val reportService: ReviewReportService,
    private val transactional: Transactional
) {

    fun getReasons(): List<ReviewReportReasonGetApi.Response> =
        reasonService.getReasons()

    suspend fun addReport(
        reviewId: Long,
        memberId: Long,
        request: ReviewReportAddApi.Request
    ): Long = transactional {
        if (!reviewService.existsById(reviewId)) {
            throw BusinessException(ReviewErrorCode.R_001)
        }

        val validated = reasonService.validateReasonAndContent(request)

        return@transactional reportService.addReport(
            reviewId = reviewId,
            memberId = memberId,
            reasonCode = validated.reasonCode,
            content = validated.content
        )
    }

}