package product.product.domain.table

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

enum class SyncJobType { PRODUCT_ES_INITIAL_LOAD }
enum class SyncJobStatus { QUEUED, RUNNING, SUCCEEDED, FAILED }

@Table("sync_job")
data class SyncJob(
    @Id
    @Column("job_id")
    override val id: Long = 0L,

    @Column("type")
    val type: SyncJobType,

    @Column("status")
    val status: SyncJobStatus,

    @Column("requested_by")
    val requestedBy: Long,

    @Column("page_size")
    val pageSize: Int,

    @Column("offset")
    val offset: Long = 0L,

    @Column("processed_count")
    val processedCount: Long = 0L,

    @Column("started_at")
    val startedAt: Instant? = null,

    @Column("finished_at")
    val finishedAt: Instant? = null,

    @Column("error_message")
    val errorMessage: String? = null
): LongIdEntity()