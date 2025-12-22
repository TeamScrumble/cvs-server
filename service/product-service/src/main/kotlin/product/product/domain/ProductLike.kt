package product.product.domain

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("product_like")
data class ProductLike(
    @Id
    @Column("product_like_id")
    override val id: Long = 0,

    @Column("product_id")
    val productId: Long,

    @Column("member_id")
    val memberId: Long
) : LongIdEntity()