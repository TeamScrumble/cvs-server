package product.product.presentation.kafka.sync

import cvs.event.Event

object ProductEsSyncEvent {
    const val TOPIC = "product.es.sync.request"

    data class Payload(
        val jobId: Long,
        val pageSize: Int
    ) : Event
}