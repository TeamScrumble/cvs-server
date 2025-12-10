package messagebroker.extension

import cvs.event.Event
import error.exception.InternalServerException
import kotlin.reflect.KClass

internal fun extractTopicFromPayload(payloadClass: KClass<out Event>): String {
    val outerClass = payloadClass.java.declaringClass
        ?: throw InternalServerException("Payload가 어떤 Event object에 속한 nested class인지 찾지 못했습니다: $payloadClass")

    val topicField = outerClass.fields
        .firstOrNull { it.name == "TOPIC" }
        ?: throw InternalServerException("Event object에 TOPIC 상수가 없습니다: ${outerClass.name}")

    return topicField.get(null) as? String
        ?: throw InternalServerException("TOPIC 상수가 String이 아닙니다: ${outerClass.name}")
}