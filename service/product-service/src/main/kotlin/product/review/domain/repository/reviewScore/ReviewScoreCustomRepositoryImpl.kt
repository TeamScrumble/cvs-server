package product.review.domain.repository.reviewScore

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import product.review.domain.entity.ReviewScore
import product.review.domain.projection.ReviewStatProjection
import product.review.domain.repository.r2dbc.bindList
import product.review.domain.repository.r2dbc.buildInClause

class ReviewScoreCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : ReviewScoreCustomRepository {
    override suspend fun findAllByReviewIds(
        reviewIds: List<Long>
    ): List<ReviewScore> {
        if(reviewIds.isEmpty()) return emptyList()

        val inClause = buildInClause(
            paramPrefix = "id",
            size = reviewIds.size
        )
        val sql = """
            SELECT rs.score_id,
                    rs.review_id,
                    rs.option_id
            FROM review_aspect_score rs
            WHERE rs.review_id IN ($inClause)
        """.trimIndent()

        val spec = databaseClient
            .sql(sql)
            .bindList("id", reviewIds)

        return spec
            .map { row, _ ->
                ReviewScore(
                    id = row.get("score_id", java.lang.Long::class.java)!!.toLong(),
                    reviewId = row.get("review_id", java.lang.Long::class.java)!!.toLong(),
                    optionId = row.get("option_id", java.lang.Long::class.java)!!.toLong()
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }

    override suspend fun findStatsByProductId(
        productId: Long
    ): List<ReviewStatProjection> {
        val sql = """
            SELECT 
                ras.option_id AS option_id, 
                COUNT(*) AS count
            FROM review_aspect_score ras
            JOIN review r ON r.review_id = ras.review_id
            WHERE r.product_id = :productId
              AND r.is_deleted = 0
            GROUP BY ras.option_id
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("productId", productId)
            .map { row, _ ->
                val optionId = row.get("option_id", java.lang.Long::class.java)!!.toLong()
                val count = row.get("count", java.lang.Long::class.java)!!.toLong()

                ReviewStatProjection(
                    optionId = optionId,
                    count = count
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }

}