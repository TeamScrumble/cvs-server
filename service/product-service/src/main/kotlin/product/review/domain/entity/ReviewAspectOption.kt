package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review_aspect_option")
data class ReviewAspectOption(
    @Id
    @Column("option_id")
    override val id: Long = 0L,

    @Column("aspect_id")
    val aspectId: Long,

    @Column("option_text")
    val optionText: String,

    @Column("display_order")
    val order: Int

) : LongIdEntity()
