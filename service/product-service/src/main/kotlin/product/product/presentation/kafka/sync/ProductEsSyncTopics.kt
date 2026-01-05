package product.product.presentation.kafka.sync

object ProductEsSyncTopics {
    const val REQUEST = "product.es.sync.request"
}

data class ProductEsSyncRequestedEvent(
    val jobId: Long,
    val pageSize: Int
)