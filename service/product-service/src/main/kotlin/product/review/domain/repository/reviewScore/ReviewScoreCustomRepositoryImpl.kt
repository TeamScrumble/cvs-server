package product.review.domain.repository.reviewScore

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import product.review.domain.entity.ReviewScore

class ReviewScoreCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : ReviewScoreCustomRepository {
    override suspend fun findAllByReviewIds(
        reviewIds: List<Long>
    ): List<ReviewScore> {
        if(reviewIds.isEmpty()) return emptyList()

        val inClause = reviewIds.joinToString(",") { it.toString() }
        val sql = """
            SELECT rs.score_id,
                    rs.review_id,
                    rs.option_id
            FROM review_aspect_score rs
            WHERE rs.review_id IN ($inClause)
        """.trimIndent()


        return databaseClient.sql(sql)
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

}