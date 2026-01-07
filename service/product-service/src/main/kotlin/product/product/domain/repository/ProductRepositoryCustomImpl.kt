package product.product.domain.repository

import cvs.crawler.CvsTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.product.domain.table.Product
import kotlin.text.get

@Repository
class ProductRepositoryCustomImpl(
    private val client: DatabaseClient
) : ProductRepositoryCustom {
    override suspend fun upsertAll(products: List<Product>, crawlRunId: String): List<Long> {
        if (products.isEmpty()) return emptyList()

        val runIdEscaped = escape(crawlRunId)

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
                0,
                '$runIdEscaped',
                NOW(),
                NOW()
            )"""
        }

        val sql = """
            INSERT INTO product (
                cvs_product_id, cvs_target, title, img, price, event, is_new, like_count,
                is_deleted, crawl_run_id,
                created_at, last_modified_at
            ) VALUES 
                $values
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                img = VALUES(img),
                price = VALUES(price),
                event = VALUES(event),
                is_new = VALUES(is_new),
                is_deleted = 0,
                crawl_run_id = VALUES(crawl_run_id),
                last_modified_at = NOW()
        """.trimIndent()

        client.sql(sql)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

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

    override suspend fun getLikeCount(productId: Long): Int {
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

    override suspend fun incrementLikeCount(productId: Long): Long {
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

    override suspend fun decrementLikeCount(productId: Long): Long {
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

    override fun findIdsToSoftDelete(target: CvsTarget, crawlRunId: String): Flow<Long> {
        val sql = """
            SELECT product_id
            FROM product
            WHERE cvs_target = :target
              AND is_deleted = 0
              AND (crawl_run_id IS NULL OR crawl_run_id <> :runId)
        """.trimIndent()

        return client.sql(sql)
            .bind("target", target.name)
            .bind("runId", crawlRunId)
            .map { row, _ -> (row.get("product_id") as Number).toLong() }
            .all()
            .asFlow()
    }

    override suspend fun softDeleteByIds(ids: List<Long>): Long {
        if (ids.isEmpty()) return 0

        val (inClause, bindNames) = buildInClause("id", ids.size)

        val sql = """
            UPDATE product
            SET is_deleted = 1,
                last_modified_at = NOW()
            WHERE is_deleted = 0
              AND product_id IN ($inClause)
        """.trimIndent()

        var spec = client.sql(sql)
        bindNames.forEachIndexed { idx, name ->
            spec = spec.bind(name, ids[idx])
        }

        return spec.fetch().rowsUpdated().awaitSingle()
    }

    private fun buildInClause(prefix: String, size: Int): Pair<String, List<String>> {
        val names = (0 until size).map { "$prefix$it" }
        val clause = names.joinToString(",") { ":$it" }
        return clause to names
    }

    private fun escape(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
}