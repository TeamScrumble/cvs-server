package product.review.domain.repository.reviewImage

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewImg

@Repository
class ReviewImgCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : ReviewImgCustomRepository {

    override suspend fun findAllByReviewIds(
        reviewIds: List<Long>
    ): List<ReviewImg> {
        if (reviewIds.isEmpty()) return emptyList()

        val unionSql = reviewIds.joinToString(" UNION ALL ") {
            "SELECT $it AS review_id"
        }
        val sql = """
            SELECT 
                ri.review_img_id,
                ri.review_id,
                ri.img_url,
                ri.display_order 
            FROM review_img ri
            JOIN (
                $unionSql
            ) r ON ri.review_id = r.review_id
            WHERE ri.is_deleted = 0
        """

        return databaseClient.sql(sql)
            .map { row, _ ->
                ReviewImg(
                    id = row.get("review_img_id", java.lang.Long::class.java)!!.toLong(),
                    reviewId = row.get("review_id", java.lang.Long::class.java)!!.toLong(),
                    imgUrl = row.get("img_url", String::class.java)!!,
                    displayOrder = row.get("display_order", Integer::class.java)!!.toInt(),
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }

}