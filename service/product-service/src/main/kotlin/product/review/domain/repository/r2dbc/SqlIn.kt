package product.review.domain.repository.r2dbc

import org.springframework.r2dbc.core.DatabaseClient

internal fun buildInClause(
    paramPrefix: String,
    size: Int
): String = (0 until size)
    .joinToString(",") { ":$paramPrefix$it" }

internal fun <T : Any> DatabaseClient.GenericExecuteSpec.bindList(
    paramPrefix: String,
    values: List<T>
): DatabaseClient.GenericExecuteSpec {
    var spec = this
    values.forEachIndexed { idx, v ->
        spec = spec.bind("$paramPrefix$idx", v)
    }
    return spec
}