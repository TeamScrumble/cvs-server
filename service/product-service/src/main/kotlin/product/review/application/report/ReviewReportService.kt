package product.review.application.report

import org.springframework.stereotype.Service
import product.review.domain.entity.ReviewReport
import product.review.domain.repository.reviewReport.ReviewReportRepository
import review.report.ReviewReportAddApi

@Service
class ReviewReportService(
    private val reportRepository: ReviewReportRepository
) {

    suspend fun addReport(
        reviewId: Long,
        memberId: Long,
        reasonCode: String,
        content: String
    ): Long {
        val report = ReviewReport(
            reviewId = reviewId,
            memberId = memberId,
            reasonCode = reasonCode,
            content = content
        )

        val saved = reportRepository.save(report)

        return saved.id
    }

}