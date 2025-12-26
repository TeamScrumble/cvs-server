package product.product.domain.repository

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.product.domain.table.Product

@Repository
class ProductCustomRepository(
    private val client: DatabaseClient
) {
    suspend fun upsertAll(products: List<Product>): Long {
        if (products.isEmpty()) return 0

        val values = products.joinToString(",") {
            """(
                ${it.cvsProductId},
                '${it.cvsTarget}',
                '${escape(it.title)}',
                '${escape(it.img)}',
                ${it.price},
                '${escape(it.event)}',
                ${if (it.isNewProduct) 1 else 0},
                0,
                NOW(),
                NOW()
            )"""
        }

        val sql = """
            INSERT INTO product (
                cvs_product_id, cvs_target, title, img, price, event, is_new, like_count,
                created_at, last_modified_at
            ) VALUES 
                $values
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                img = VALUES(img),
                price = VALUES(price),
                event = VALUES(event),
                is_new = VALUES(is_new),
                last_modified_at = NOW()
        """

        return client.sql(sql)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    suspend fun incrementLikeCount(productId: Long): Long {
        val sql = """
            UPDATE product
            SET like_count = like_count + 1,
                last_modified_at = NOW()
            WHERE product_id = :productId
        """.trimIndent()

        return client.sql(sql)
            .bind("productId", productId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    suspend fun decrementLikeCount(productId: Long): Long {
        val sql = """
            UPDATE product
            SET like_count = GREATEST(like_count - 1, 0),
                last_modified_at = NOW()
            WHERE product_id = :productId
        """.trimIndent()

        return client.sql(sql)
            .bind("productId", productId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    suspend fun getLikeCount(productId: Long): Int {
        val sql = """
            SELECT like_count
            FROM product
            WHERE product_id = :productId
        """.trimIndent()

        return client.sql(sql)
            .bind("productId", productId)
            .map { row, _ -> (row.get("like_count") as Number).toInt() }
            .one()
            .awaitSingle()
    }

    private fun escape(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
}