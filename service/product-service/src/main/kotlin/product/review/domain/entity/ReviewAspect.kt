package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review_aspect")
data class ReviewAspect(
    @Id
    @Column("aspect_id")
    override val id: Long = 0L,

    @Column("title")
    val title: String,

    @Column("question")
    val question: String

) : LongIdEntity()