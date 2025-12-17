package product.review.domain.entity

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review_img")
data class ReviewImg(
    @Id
    @Column("review_img_id")
    override val id: Long = 0L,

    @Column("review_id")
    val reviewId: Long,

    @Column("img_url")
    val imgUrl: String,

    @Column("display_order")
    val displayOrder: Int

) : LongIdEntity()
