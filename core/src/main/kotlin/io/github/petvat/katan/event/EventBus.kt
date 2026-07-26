package io.github.petvat.katan.event

import kotlin.reflect.KClass

/**
 * Event bus that attaches function handlers to event objects.
 */
//object EventBus {
//    private val listeners = mutableMapOf<KClass<*>, MutableList<(Any) -> Unit>>()
//
//    /**
//     * Adds a new handler that triggers on each new firing of [eventClass]
//     */
//    @Suppress("UNCHECKED_CAST")
//    fun <T : Any, V : Any> on(eventClass: KClass<T>, handler: (T) -> Unit, by: KClass<V>) {
//        listeners.getOrPut(eventClass) { mutableListOf() }.add { e -> handler(e as T) }
//    }
//
//    fun remove() {
//
//    }
//
//    /**
//     * Adds a new handler that triggers on the next firing of [eventKClass] only once.
//     * After the event, the handler will be removed.
//     */
//    @Suppress("UNCHECKED_CAST")
//    fun <T : Any> once(eventKClass: KClass<T>, handler: (T) -> Unit) {
//        val wrapper: (Any) -> Unit = object : (Any) -> Unit {
//            override fun invoke(e: Any) {
//                handler(e as T)
//                listeners[eventKClass]?.remove(this)
//            }
//        }
//
//        listeners.getOrPut(eventKClass) { mutableListOf() }.add(wrapper)
//    }
//
//    /**
//     * Trigger each handler that is waiting for [event] with [event] as the argument.
//     */
//    fun fire(event: Any) {
//        listeners[event::class]?.forEach { it(event) }
//    }
//}


class EventSystem {
    private val listeners = mutableListOf<EventListener>()

    fun fire(event: Event) {
        listeners.forEach {
            it.onEvent(event)
        }
    }

    operator fun plusAssign(listener: EventListener) {
        listeners += listener
    }

    operator fun minusAssign(listener: EventListener) {
        listeners -= listener
    }
}


/**
 * Loop-back
 * Event bus for incoming responses from server.
 */
//object EventBus {
//
//    private val listeners = mutableListOf<EventListener>()
//
//
//    fun fire(event: Event) {
//        // TODO: This needs a lock!
//        listeners.forEach {
//            it.onEvent(event)
//        }
//    }
//
//    operator fun plusAssign(listener: EventListener) {
//        listeners += listener
//    }
//
//    operator fun minusAssign(listener: EventListener) {
//        listeners -= listener
//    }
//}
