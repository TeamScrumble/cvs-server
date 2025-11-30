package messagebroker.example

import cvs.event.Event

object ExampleEvent {

    /**
     * TOPIC 이라는 정적 필드를 찾아 topic을 자동으로 부여합니다.
     * 꼭 TOPIC이라는 정적 필드를 넣어주세요.
     */
    const val TOPIC = "example-topic"

    data class Payload(
        val data: String
    ) : Event
}
