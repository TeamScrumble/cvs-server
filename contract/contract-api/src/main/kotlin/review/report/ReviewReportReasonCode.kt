package review.report

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException

enum class ReviewReportReasonCode(
    val description: String
) {
    IRRELEVANT("상품과 무관한 내용 또는 이미지"),
    ADVERTISEMENT("광고•홍보 목적의 내용"),
    PERSONAL_INFO("개인정보 노출"),
    OTHER("기타");

    companion object {
        fun list(): List<ReviewReportReasonGetApi.Response> =
            entries.map { ReviewReportReasonGetApi.Response(it.name, it.description) }

        fun from(code: String): ReviewReportReasonCode =
            entries.firstOrNull { it.name.equals(code, ignoreCase = true) }
                ?: throw BusinessException(ReviewErrorCode.R_011)
    }

}
