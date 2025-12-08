package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review_like")
data class ReviewLike(
    @Id
    @Column("review_like_id")
    override val id: Long = 0L,

    @Column("review_id")
    val reviewId: Long,

    @Column("member_id")
    val memberId: Long

) : LongIdEntity()
