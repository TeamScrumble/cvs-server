package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review_aspect_score")
data class ReviewScore(
    @Id
    @Column("score_id")
    override val id: Long = 0,

    @Column("review_id")
    val reviewId: Long,

    @Column("option_id")
    val optionId: Long

) : LongIdEntity()
