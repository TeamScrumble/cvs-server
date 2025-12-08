package product.review.domain.entity

import db.base.LongIdEntity
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
    val content: String

) : LongIdEntity()
