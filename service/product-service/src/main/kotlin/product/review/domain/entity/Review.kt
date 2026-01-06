package product.review.domain.entity

import db.base.LongIdEntity
import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review")
data class Review (
    @Id
    @Column("review_id")
    override val id: Long = 0L,

    @Column("product_id")
    val productId: Long,

    @Column("member_id")
    val memberId: Long,

    @Column("rating")
    val rating: Int,

    @Column("content")
    val content: String,

    @Column("is_deleted")
    val isDeleted: Boolean = false,

    @Column("is_receipt") // 영수증 인증 여부
    val isReceipt: Boolean,

    @Column("like_count")
    val likeCount: Long = 0L

) : LongIdEntity() {

    init {
        // 만족도 범위 체크
        if (rating !in RATING_MIN..RATING_MAX) {
            throw BusinessException(ReviewErrorCode.R_002)
        }
        // 리뷰 글자수 체크
        val contentLength = content.trim().length
        if (contentLength !in CONTENT_MIN..CONTENT_MAX) {
            throw BusinessException(ReviewErrorCode.R_003)
        }
    }

    companion object {
        // 별점 1~5
        private const val RATING_MIN = 1
        private const val RATING_MAX = 5
        // 리뷰 글자 수 10~500
        private const val CONTENT_MIN = 10
        private const val CONTENT_MAX = 500

        fun create(
            productId: Long,
            memberId: Long,
            rating: Int,
            content: String,
            isReceipt: Boolean
        ): Review {
            return Review(
                productId = productId,
                memberId = memberId,
                rating = rating,
                content = content,
                isReceipt = isReceipt
            )
        }
    }

}
