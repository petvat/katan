package io.github.petvat.katan.event

import com.badlogic.gdx.Gdx

class EventSystem {
    private val listeners = mutableListOf<EventListener>()

    fun fire(event: Event) = Gdx.app.postRunnable { dispatchToListeners(event) }

    private fun dispatchToListeners(event: Event) {
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
