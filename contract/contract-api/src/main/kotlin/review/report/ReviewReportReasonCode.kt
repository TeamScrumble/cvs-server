package review.report

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException

enum class ReviewReportReasonCode(
    val code: String,
    val description: String
) {
    IRRELEVANT("IRRELEVANT", "상품과 무관한 내용 또는 이미지"),
    ADVERTISEMENT("ADVERTISEMENT", "광고•홍보 목적의 내용"),
    PERSONAL_INFO("PERSONAL_INFO", "개인정보 노출"),
    OTHER("OTHER", "기타");

    companion object {
        fun list(): List<ReviewReportReasonGetApi.Response> =
            entries.map { ReviewReportReasonGetApi.Response(it.code, it.description) }

        fun from(code: String): ReviewReportReasonCode =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: throw BusinessException(ReviewErrorCode.R_011)
    }

}
