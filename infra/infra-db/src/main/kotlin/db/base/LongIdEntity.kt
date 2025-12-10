package db.base

import org.springframework.data.domain.Persistable

abstract class LongIdEntity : Persistable<Long>, BaseEntity() {
    abstract val id: Long

    override fun getId(): Long = id

    override fun isNew(): Boolean = id == 0L
}