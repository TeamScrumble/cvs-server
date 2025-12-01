package messagebroker.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.event.Event
import jakarta.annotation.PostConstruct
import messagebroker.extension.extractTopicFromPayload
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.listener.MessageListener
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

@Suppress("UNCHECKED_CAST")
@Component
class EventConsumerRegistrar(
    private val consumers: List<EventConsumer<*>>,
    private val containerFactory: ConcurrentKafkaListenerContainerFactory<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @PostConstruct
    fun register() {
        consumers.forEach { consumer ->

            val payloadClass = extractPayloadClass(consumer)
            val topic = extractTopicFromPayload(payloadClass)

            val container = containerFactory.createContainer(topic)
            container.containerProperties.messageListener =
                MessageListener<String, String> { record ->
                    dispatch(consumer, payloadClass, record.value())
                }

            container.start()
        }
    }

    private fun extractPayloadClass(consumer: EventConsumer<*>): KClass<out Event> {
        val superType = consumer::class.supertypes
            .first { it.jvmErasure == EventConsumer::class }

        val payloadType = superType.arguments[0].type
            ?: throw IllegalStateException("EventConsumer 제네릭 타입 정보를 찾을 수 없습니다: ${consumer::class}")

        return payloadType.jvmErasure as KClass<out Event>
    }

    private fun dispatch(
        consumer: EventConsumer<*>,
        payloadClass: KClass<out Event>,
        raw: String,
    ) {
        val payload = objectMapper.readValue(raw, payloadClass.java)

        @Suppress("UNCHECKED_CAST")
        (consumer as EventConsumer<Event>).consume(payload)
    }
}