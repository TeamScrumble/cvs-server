package gateway.docs

data class ServiceResult(
    val info: ServiceInfo,
    val spec: Map<String, Any?>?,
    val alive: Boolean,
    val fromCache: Boolean,
)
