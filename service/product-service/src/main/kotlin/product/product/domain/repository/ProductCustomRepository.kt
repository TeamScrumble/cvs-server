package product.product.domain.repository

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.product.domain.table.Product

@Repository
class ProductCustomRepository(
    private val client: DatabaseClient
) {
    suspend fun upsertAll(products: List<Product>): List<Long> {
        if (products.isEmpty()) return emptyList()

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
        """.trimIndent()

        // 1) upsert 실행
        client.sql(sql)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        // 2) 이번에 처리한 cvs_product_id들로 product_id를 다시 조회해서 반환
        val cvsIds = products.map { it.cvsProductId }.distinct()
        return findProductIdsByCvsProductIds(cvsIds)
    }

    private suspend fun findProductIdsByCvsProductIds(cvsProductIds: List<Long>): List<Long> {
        if (cvsProductIds.isEmpty()) return emptyList()

        val chunkSize = 1000
        val result = mutableListOf<Long>()

        for (chunk in cvsProductIds.chunked(chunkSize)) {
            val inClause = chunk.joinToString(",") { it.toString() } // 숫자라 안전
            val selectSql = """
                SELECT product_id
                FROM product
                WHERE cvs_product_id IN ($inClause)
            """.trimIndent()

            val ids = client.sql(selectSql)
                .map { row, _ -> (row.get("product_id") as Number).toLong() }
                .all()
                .collectList()
                .awaitSingle()

            result.addAll(ids)
        }

        return result
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