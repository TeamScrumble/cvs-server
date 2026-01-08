package messagebroker.base

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Semaphore
import org.slf4j.LoggerFactory

open class KafkaEventBase(max: Int = 4) {
    companion object {
        const val CHUNK_SIZE = 800
    }

    protected val logger = LoggerFactory.getLogger(this::class.java)!!

    protected val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        logger.error("응답 처리 중 처리되지 않은 예외 발생: ${ex.message}", ex)
    }

    protected val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    protected val semaphore = Semaphore(max)

    @PreDestroy
    fun shutdown() {
        workerScope.cancel()
    }
}