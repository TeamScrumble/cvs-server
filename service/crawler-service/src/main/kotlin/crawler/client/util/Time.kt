package crawler.client.util

import org.slf4j.Logger

fun calculateTimeMillis(logger: Logger, prefix: String, durationMillis: Long) {
    val minutes = durationMillis / 1000 / 60
    val seconds = (durationMillis / 1000) % 60
    val millis = durationMillis % 1000

    logger.info("[$prefix] ${minutes}분 ${seconds}초 ${millis}ms 소요")
}