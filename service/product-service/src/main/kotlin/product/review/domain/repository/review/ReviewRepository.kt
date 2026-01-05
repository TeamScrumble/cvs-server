package product.review.domain.repository.review

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review
import java.time.LocalDateTime

@Repository
interface ReviewRepository :
    CoroutineCrudRepository<Review, Long>,
    ReviewCustomRepository
{

    // 상품 리뷰 삭제
    @Modifying
    @Query(
        """
        UPDATE review 
            SET is_deleted = 1,
                last_modified_at = :lastModifiedAt 
        WHERE review_id = :reviewId 
            AND is_deleted = 0
        """
    )
    suspend fun softDeleteActiveReview(
        reviewId: Long,
        lastModifiedAt: LocalDateTime
    ): Int

    suspend fun findByIdAndIsDeletedFalse(id: Long): Review?

    // 상품 총 리뷰 수
    suspend fun countByProductIdAndIsDeletedFalse(
        productId: Long
    ): Long

    // 영수증 인증 리뷰 수
    suspend fun countByProductIdAndIsReceiptTrueAndIsDeletedFalse(
        productId: Long
    ): Long

    // 평균 별점
    @Query(
        """
        SELECT CAST(AVG(r.rating) AS DOUBLE) AS avgRating
        FROM review r
        WHERE r.product_id = :productId AND r.is_deleted = 0
        """
    )
    suspend fun findAvgRatingByProductId(productId: Long): Double?
}