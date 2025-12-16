package product.review.domain.repository.reviewAspect

import product.review.domain.entity.ReviewAspectOption

interface AspectOptionCustomRepository {
    /*
    option_id 목록으로 옵션 메타 조회
    - display_order 기준 정렬
     */
    suspend fun findAllByIdInOrderByDisplayOrderAsc(
        ids: Iterable<Long>
    ): List<ReviewAspectOption>

    /*
    aspect_id 목록으로 하위 옵션 조회
    - aspect_id ASC, display_order ASC
     */
    suspend fun findAllByAspectIdInOrderByAspectAndDisplay(
        aspectIds: Iterable<Long>
    ): List<ReviewAspectOption>


}