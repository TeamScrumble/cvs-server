package product.review.domain.repository.reviewAspect

import io.r2dbc.spi.Row
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspect
import product.review.domain.repository.r2dbc.bindList
import product.review.domain.repository.r2dbc.buildInClause

@Repository
class AspectCustomRepositoryImpl(
    private val dbClient: DatabaseClient
) : AspectCustomRepository {
    override suspend fun findAllByIdInOrderByIdAsc(
        ids: Iterable<Long>
    ): List<ReviewAspect> {
        val idList = ids.toList()
        if (idList.isEmpty()) return emptyList()

        val inClause = buildInClause(
            paramPrefix = "id",
            size = idList.size
        )
        val sql = """
        SELECT * 
        FROM review_aspect 
        WHERE aspect_id IN ($inClause) 
        ORDER BY aspect_id ASC
        """.trimIndent()

        val spec = dbClient
            .sql(sql)
            .bindList("id", idList)

        return spec
            .map { row, _ -> rowToReviewAspect(row) }
            .all()
            .collectList()
            .awaitSingle()
    }

    private fun rowToReviewAspect(row: Row): ReviewAspect {
        return ReviewAspect(
            id = row.get("aspect_id", java.lang.Long::class.java)!!.toLong(),
            title = row.get("title", String::class.java)!!,
            question = row.get("question", String::class.java)!!
        )
    }

}