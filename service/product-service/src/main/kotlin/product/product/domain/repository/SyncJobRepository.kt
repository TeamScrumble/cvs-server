package product.product.domain.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import product.product.domain.table.SyncJob
import java.time.Instant

interface SyncJobRepository : CoroutineCrudRepository<SyncJob, Long> {

    @Query(
        """
        SELECT *
        FROM sync_job
        WHERE type = :type
          AND status IN ('QUEUED', 'RUNNING')
        ORDER BY job_id DESC
        LIMIT 1
        """
    )
    fun findLatestActiveJob(type: String): Flow<SyncJob>

    @Query(
        """
        UPDATE sync_job
        SET status = :status,
            started_at = :startedAt,
            last_modified_at = :lastModifiedAt
        WHERE job_id = :jobId
        """
    )
    suspend fun markRunning(
        jobId: Long,
        status: String,
        startedAt: Instant,
        lastModifiedAt: Instant
    ): Int

    @Query(
        """
        UPDATE sync_job
        SET status = :status,
            finished_at = :finishedAt,
            last_modified_at = :lastModifiedAt
        WHERE job_id = :jobId
        """
    )
    suspend fun markFinished(
        jobId: Long,
        status: String,
        finishedAt: Instant,
        lastModifiedAt: Instant
    ): Int

    @Query(
        """
        UPDATE sync_job
        SET status = :status,
            finished_at = :finishedAt,
            error_message = :errorMessage,
            last_modified_at = :lastModifiedAt
        WHERE job_id = :jobId
        """
    )
    suspend fun markFailed(
        jobId: Long,
        status: String,
        finishedAt: Instant,
        errorMessage: String,
        lastModifiedAt: Instant
    ): Int

    @Query(
        """
        UPDATE sync_job
        SET offset = :offset,
            processed_count = :processedCount,
            last_modified_at = :lastModifiedAt
        WHERE job_id = :jobId
        """
    )
    suspend fun updateProgress(
        jobId: Long,
        offset: Long,
        processedCount: Long,
        lastModifiedAt: Instant
    ): Int
}