package product.review.domain.repository.review

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review
import product.review.domain.projection.ReviewSortingStatProjection

@Repository
interface ReviewRepository : CoroutineCrudRepository<Review, Long> {

    // 상품 리뷰 목록 조회
    @Query(
        """
        SELECT * 
        FROM review 
        WHERE product_id = :productId
            AND is_deleted = 0 
        ORDER BY last_modified_at DESC 
        LIMIT :size OFFSET :offset
        """
    )
    suspend fun findAllByProductIdAndIsDeletedFalse(
        productId: Long,
        offset: Int,
        size: Int
    ): List<Review>

    // 상품 총 리뷰 수
    suspend fun countByProductIdAndIsDeletedFalse(
        productId: Long
    ): Long

    // 영수증 인증 리뷰 수
    suspend fun countByProductIdAndIsReceiptTrueAndIsDeletedFalse(
        productId: Long
    ): Long

    @Query(
        """
        SELECT 
            r.review_id AS reviewId,
            COUNT(rl.review_like_id) AS likeCount,
            EXISTS(
                SELECT 1 FROM review_img ri WHERE ri.review_id = r.review_id
            ) AS hasMedia,
            r.created_at AS createdAt
        FROM review r
        LEFT JOIN review_like rl ON r.review_id = rl.review_id
        WHERE r.product_id = :productId AND r.is_deleted = 0
        GROUP BY r.review_id, r.created_at
        """
    )
    suspend fun findReviewStatsForSorting(productId: Long): List<ReviewSortingStatProjection>
}