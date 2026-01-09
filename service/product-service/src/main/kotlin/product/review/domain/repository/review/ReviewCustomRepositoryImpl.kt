package product.review.domain.repository.review

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review
import review.ReviewSortType
import review.ReviewListApi

@Repository
class ReviewCustomRepositoryImpl(
    private val client: DatabaseClient
) : ReviewCustomRepository {

    override suspend fun findList(
        productId: Long,
        request: ReviewListApi.Request,
        offset: Int
    ): List<Review> {
        val orderBy = when (request.sort) {
            ReviewSortType.RECOMMENDED ->
                "likeCount DESC, hasMedia DESC, r.last_modified_at DESC"
            ReviewSortType.LATEST ->
                "r.last_modified_at DESC"
            ReviewSortType.RATING_HIGH ->
                "r.rating DESC, r.last_modified_at DESC"
            ReviewSortType.RATING_LOW ->
                "r.rating ASC, r.last_modified_at DESC"
            ReviewSortType.MOST_HELPFUL ->
                "likeCount DESC, r.last_modified_at DESC"
            else -> "likeCount DESC, hasMedia DESC, r.last_modified_at DESC"
        }

        val sql = """
            SELECT
                r.review_id AS id,
                r.product_id AS productId,
                r.member_id AS memberId,
                r.rating AS rating,
                r.content AS content,
                r.is_receipt AS isReceipt,
                r.is_deleted AS isDeleted,
                r.like_count As likeCount,
                r.created_at AS createdAt,
                r.last_modified_at AS lastModifiedAt,
                EXISTS (
                    SELECT 1 FROM review_img ri WHERE ri.review_id = r.review_id
                ) AS hasMedia
            FROM review r
            WHERE r.product_id = :productId
              AND r.is_deleted = 0
              AND (:receiptOnly = FALSE OR r.is_receipt = TRUE)
              AND (:imageOnly = FALSE OR EXISTS (
                    SELECT 1 FROM review_img ri2 WHERE ri2.review_id = r.review_id
                  ))
            ORDER BY $orderBy
            LIMIT :size OFFSET :offset
        """.trimIndent()

        return client.sql(sql)
            .bind("productId", productId)
            .bind("receiptOnly", request.receiptOnly)
            .bind("imageOnly", request.imageOnly)
            .bind("size", request.pageSize)
            .bind("offset", offset)
            .mapProperties(Review::class.java)
            .all()
            .collectList()
            .awaitSingleOrNull() ?: emptyList()
    }

    override suspend fun getLikeCount(reviewId: Long): Long {
        val sql = """
            SELECT like_count 
            FROM review 
            WHERE review_id = :reviewId
        """.trimIndent()

        return client.sql(sql)
            .bind("reviewId", reviewId)
            .map { row, _ ->
                (row.get("like_count") as Number).toLong()
            }
            .one()
            .awaitSingle()
    }

    override suspend fun incrementLikeCount(reviewId: Long) {
        val sql = """
            UPDATE review 
            SET like_count = like_count + 1 
            WHERE review_id = :reviewId
        """.trimIndent()

        client.sql(sql)
            .bind("reviewId", reviewId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    override suspend fun decrementLikeCount(reviewId: Long) {
        val sql = """
            UPDATE review 
            SET like_count = GREATEST(like_count - 1, 0) 
            WHERE review_id = :reviewId
        """.trimIndent()

        client.sql(sql)
            .bind("reviewId", reviewId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

}