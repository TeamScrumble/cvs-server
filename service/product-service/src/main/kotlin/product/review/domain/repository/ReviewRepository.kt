package product.review.domain.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.Review
import reactor.core.publisher.Flux

@Repository
interface ReviewRepository : CoroutineCrudRepository<Review, Long> {

    // 상품 리뷰 목록 조회
    suspend fun findByProductIdAndIsDeletedFalse(
        productId: Long,
        pageable: Pageable
    ): Flux<Review>

    // 상품 총 리뷰 수
    suspend fun countByProductIdAndIsDeletedFalse(
        productId: Long
    ): Long

    // 영수증 인증 리뷰 수
    suspend fun countByProductIdAndIsReceiptTrueAndIsDeletedFalse(
        productId: Long
    ): Long
}