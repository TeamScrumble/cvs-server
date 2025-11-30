package messagebroker.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.event.Event
import messagebroker.extension.extractTopicFromPayload
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Component
class EventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    private val topicCache: MutableMap<KClass<out Event>, String> = ConcurrentHashMap()

    fun publish(event: Event) {
        val topic = resolveTopic(event::class)
        val json = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(topic, json)
    }

    private fun resolveTopic(payloadClass: KClass<out Event>): String {
        return topicCache.getOrPut(payloadClass) {
            extractTopicFromPayload(payloadClass)
        }
    }
}