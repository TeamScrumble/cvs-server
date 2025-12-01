package messagebroker.example

import messagebroker.consumer.EventConsumer
import org.springframework.stereotype.Component

@Component
class ExampleEventConsumer : EventConsumer<ExampleEvent.Payload> {


    override fun consume(event: ExampleEvent.Payload) {
        println(event)
        // consuming logics
    }
}