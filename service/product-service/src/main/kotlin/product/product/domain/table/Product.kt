package product.product.domain.table

import cvs.crawler.CvsTarget
import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product")
data class Product(
    @Id
    @Column("product_id")
    override val id: Long = 0,

    @Column("cvs_product_id")
    val cvsProductId: Long,

    @Column("cvs_target")
    val cvsTarget: CvsTarget,

    @Column("title")
    val title: String,

    @Column("img")
    val img: String,

    @Column("price")
    val price: Int,

    @Column("event")
    val event: String,

    @Column("is_new")
    val isNewProduct: Boolean,

    @Column("like_count")
    val likeCount: Int,
) : LongIdEntity()