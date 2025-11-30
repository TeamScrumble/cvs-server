package messagebroker.consumer

import cvs.event.Event

interface EventConsumer<T : Event> {
    fun consume(event: T)
}