package db.base

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

abstract class BaseEntity {
    @CreatedDate
    @Column("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column("last_modified_at")
    var lastModifiedAt: LocalDateTime = LocalDateTime.now()
}