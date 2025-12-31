package product.review.application.report

import db.transactional.Transactional
import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.stereotype.Service
import passport.Passport
import product.common.valid.MemberValidService
import product.review.application.ReviewService
import review.report.ReviewReportAddApi
import review.report.ReviewReportReasonGetApi

@Service
class ReviewReportFacade(
    private val memberValidService: MemberValidService,
    private val reviewService: ReviewService,
    private val reasonService: ReviewReportReasonService,
    private val reportService: ReviewReportService,
    private val transactional: Transactional
) {

    fun getReasons(): List<ReviewReportReasonGetApi.Response> =
        reasonService.getReasons()

    suspend fun addReport(
        passport: Passport,
        reviewId: Long,
        request: ReviewReportAddApi.Request
    ): Long = transactional {
        memberValidService.validateMember(passport)
        if (!reviewService.existsById(reviewId)) {
            throw BusinessException(ReviewErrorCode.R_001)
        }

        val validated = reasonService.validateReasonAndContent(request)

        return@transactional reportService.addReport(
            reviewId = reviewId,
            memberId = passport.memberId,
            reasonCode = validated.reasonCode,
            content = validated.content
        )
    }

}