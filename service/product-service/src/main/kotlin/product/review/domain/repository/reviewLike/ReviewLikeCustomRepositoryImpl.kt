package product.review.domain.repository.reviewLike

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository

@Repository
class ReviewLikeCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : ReviewLikeCustomRepository {
    override suspend fun countLikesByReviewIds(
        reviewIds: List<Long>
    ): Map<Long, Int> {
        if (reviewIds.isEmpty()) return emptyMap()

        val unionSql = reviewIds.joinToString(" UNION ALL ") {
            "SELECT $it AS review_id"
        }
        val sql = """
            SELECT rl.review_id, COUNT(rl.review_like_id) AS cnt
            FROM review_like rl
            JOIN (
                $unionSql
            ) r ON rl.review_id = r.review_id
            GROUP BY rl.review_id
        """

        return databaseClient.sql(sql)
            .map { row, _ ->
                row.get("review_id", java.lang.Long::class.java)!!.toLong() to
                row.get("cnt", java.lang.Long::class.java)!!.toInt()
            }
            .all()
            .collectMap({ it.first }, { it.second })
            .awaitSingle()
    }

    override suspend fun findMemberLikedReviewIds(
        reviewIds: List<Long>,
        memberId: Long
    ): Set<Long> {
        if (reviewIds.isEmpty()) return emptySet()

        val unionSql = reviewIds.joinToString(" UNION ALL ") {
            "SELECT $it AS review_id"
        }
        val sql = """
            SELECT rl.review_id 
            FROM review_like rl 
            JOIN (
                $unionSql 
            ) r ON rl.review_id = r.review_id 
            WHERE rl.member_id = :memberId 
        """

        return databaseClient.sql(sql)
            .bind("memberId", memberId)
            .map { row, _ ->
                row.get("review_id", java.lang.Long::class.java)!!.toLong()
            }
            .all()
            .collectList()
            .awaitSingle()
            .toSet()
    }

    override suspend fun insertIgnore(
        reviewId: Long,
        memberId: Long
    ): Boolean {
        val sql = """
            INSERT IGNORE INTO review_like (review_id, member_id) 
            VALUES (:reviewId, :memberId)
        """.trimIndent()

        val rows = databaseClient.sql(sql)
            .bind("reviewId", reviewId)
            .bind("memberId", memberId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows?.toInt() == 1
    }

}