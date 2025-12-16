package product.review.domain.repository.reviewAspect

import io.r2dbc.spi.Row
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import product.review.domain.entity.ReviewAspectOption
import product.review.domain.repository.r2dbc.bindList
import product.review.domain.repository.r2dbc.buildInClause

@Repository
class AspectOptionCustomRepositoryImpl(
    private val dbClient: DatabaseClient
) : AspectOptionCustomRepository {

    // option_id 목록으로 옵션 메타 조회
    override suspend fun findAllByIdInOrderByDisplayOrderAsc(
        ids: Iterable<Long>
    ): List<ReviewAspectOption> {
        val idList = ids.toList()
        if (idList.isEmpty()) return emptyList()

        val inClause = buildInClause(
            paramPrefix = "id",
            size = idList.size
        )
        val sql = """
            SELECT * 
            FROM review_aspect_option 
            WHERE option_id IN ($inClause)
            ORDER BY display_order ASC
        """.trimIndent()

        val spec = dbClient
            .sql(sql)
            .bindList("id", idList)

        return spec
            .map { row, _ -> rowToReviewAspectOption(row) }
            .all()
            .collectList()
            .awaitSingle()
    }


    // aspect_id 목록으로 하위 옵션 조회
    override suspend fun findAllByAspectIdInOrderByAspectAndDisplay(
        aspectIds: Iterable<Long>
    ): List<ReviewAspectOption> {
        val idList = aspectIds.toList()
        if (idList.isEmpty()) return emptyList()

        val inClause = buildInClause(
            paramPrefix = "aspect",
            size = idList.size
        )
        val sql = """
            SELECT * 
            FROM review_aspect_option 
            WHERE aspect_id IN ($inClause)
            ORDER BY aspect_id ASC, display_order ASC
        """.trimIndent()

        val spec = dbClient
            .sql(sql)
            .bindList("aspect", idList)

        return spec
            .map { row, _ -> rowToReviewAspectOption(row) }
            .all()
            .collectList()
            .awaitSingle()
    }

    private fun rowToReviewAspectOption(row: Row): ReviewAspectOption {
        return ReviewAspectOption(
            id = row.get("option_id", java.lang.Long::class.java)!!.toLong(),
            aspectId = row.get("aspect_id", java.lang.Long::class.java)!!.toLong(),
            optionText = row.get("option_text", String::class.java)!!,
            displayOrder = row.get("display_order", java.lang.Integer::class.java)!!.toInt()
        )
    }

}