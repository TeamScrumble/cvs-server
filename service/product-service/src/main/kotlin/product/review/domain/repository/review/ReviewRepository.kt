package product.review.domain.repository.review

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review

@Repository
interface ReviewRepository :
    CoroutineCrudRepository<Review, Long>,
    ReviewCustomRepository
{

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