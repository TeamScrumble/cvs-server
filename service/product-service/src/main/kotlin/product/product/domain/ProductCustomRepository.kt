package product.product.domain

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

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
                '${it.event}',
                ${if (it.isNewProduct) 1 else 0},
                NOW(),
                NOW()
            )"""
        }

        val sql = """
            INSERT INTO product (
                cvs_product_id, cvs_target, title, img, price, event, is_new,
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

    private fun escape(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
}