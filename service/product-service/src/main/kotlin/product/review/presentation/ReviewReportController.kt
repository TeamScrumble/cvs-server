package product.review.presentation

import ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import product.review.application.report.ReviewReportFacade
import review.ReviewPaths
import review.report.ReviewReportAddApi
import review.report.ReviewReportApi
import review.report.ReviewReportReasonGetApi
import security.passport.RequestPassport

@RestController
class ReviewReportController(
    private val reportFacade: ReviewReportFacade
) : ReviewReportApi {

    @PostMapping(ReviewPaths.REPORT)
    override suspend fun reportAdd(
        @RequestPassport passport: Passport,
        @PathVariable reviewId: Long,
        @RequestBody request: ReviewReportAddApi.Request
    ): ApiResponse<ReviewReportAddApi.Response> {
        val saved = reportFacade.addReport(
            passport = passport,
            reviewId = reviewId,
            request = request
        )

        return ApiResponse.Success(ReviewReportAddApi.Response(saved))
    }

    @GetMapping(ReviewPaths.REPORT_REASONS)
    override suspend fun getReasons():
            ApiResponse<List<ReviewReportReasonGetApi.Response>> {
        val reasonList = reportFacade.getReasons()

        return ApiResponse.Success(reasonList)
    }

}