package review.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("review")
class Review (
    @Id
    @Column("review_id")
    val id: Long = 0,

    @Column("product_id")
    val productId: Long,

    @Column("member_id")
    val memberId: Long,

    @Column("rating")
    val rating: Int,

    @Column("comment")
    val comment: String,

)
