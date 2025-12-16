package product.review.domain.repository.reviewAspect

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspect

interface AspectCustomRepository {

    suspend fun findAllByIdInOrderByIdAsc(
        ids: Iterable<Long>
    ): List<ReviewAspect>

    private fun buildInClause(paramPrefix: String, size: Int): String =
        (0 until size)
            .joinToString(",") { ":$paramPrefix$it" }

}