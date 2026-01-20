package product.product.domain.table

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product_search_history")
data class ProductSearchHistory(
    @Id
    @Column("product_search_history_id")
    override val id: Long = 0,

    @Column("keyword")
    val keyword: String,
) : LongIdEntity()

