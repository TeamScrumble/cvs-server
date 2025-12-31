package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import review.report.ReviewReportStatus
import java.time.LocalDateTime

@Table("review_report")
data class ReviewReport(
    @Id
    @Column("report_id")
    override val id: Long = 0L,

    @Column("member_id")
    val memberId: Long,

    @Column("review_id")
    val reviewId: Long,

    @Column("reason_code")
    val reasonCode: String,

    @Column("content")
    val content: String,

    @Column("status")
    val status: String = ReviewReportStatus.PENDING.name,

    @Column("processed_at")
    val processedAt: LocalDateTime = LocalDateTime.now()

) : LongIdEntity()
