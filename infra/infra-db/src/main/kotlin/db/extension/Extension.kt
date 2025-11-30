package db.extension

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

suspend fun <T : Any, R> R.upsert(
    entity: T,
    findExisting: suspend R.(T) -> T?
): T where R : CoroutineCrudRepository<T, Long> {
    return try {
        save(entity)
    } catch (e: DataIntegrityViolationException) {
        findExisting(entity) ?: throw e
    }
}