package product.product.presentation.kafka.sync

import kotlinx.coroutines.launch
import messagebroker.base.KafkaEventBase
import messagebroker.consumer.EventConsumer
import org.springframework.stereotype.Component
import product.product.domain.repository.SyncJobRepository
import product.product.domain.table.SyncJobStatus
import product.product.elasticsearch.service.ProductEsSyncService
import java.time.Instant

@Component
class ProductEsSyncEventConsumer(
    private val syncJobRepository: SyncJobRepository,
    private val productEsSyncService: ProductEsSyncService
) : EventConsumer<ProductEsSyncEvent.Payload>, KafkaEventBase() {
    override fun consume(event: ProductEsSyncEvent.Payload) {
        workerScope.launch {
            val jobId = event.jobId
            val now = Instant.now()

            val job = syncJobRepository.findById(jobId)
            if (job == null) {
                logger.warn("sync job not found: jobId={}", jobId)
                return@launch
            }

            // 멱등 처리: 이미 끝났으면 무시
            if (job.status == SyncJobStatus.SUCCEEDED || job.status == SyncJobStatus.FAILED) {
                return@launch
            }

            // RUNNING으로 마킹
            syncJobRepository.markRunning(
                jobId = jobId,
                status = SyncJobStatus.RUNNING.name,
                startedAt = now,
                lastModifiedAt = now
            )

            try {
                productEsSyncService.initialLoad(jobId = jobId, pageSize = event.pageSize)
                val finishedAt = Instant.now()
                syncJobRepository.markFinished(
                    jobId = jobId,
                    status = SyncJobStatus.SUCCEEDED.name,
                    finishedAt = finishedAt,
                    lastModifiedAt = finishedAt
                )
            } catch (ex: Exception) {
                val finishedAt = Instant.now()
                syncJobRepository.markFailed(
                    jobId = jobId,
                    status = SyncJobStatus.FAILED.name,
                    finishedAt = finishedAt,
                    errorMessage = ex.message ?: "unknown error",
                    lastModifiedAt = finishedAt
                )
                logger.error("product es sync failed. jobId={}", jobId, ex)
            }
        }
    }
}