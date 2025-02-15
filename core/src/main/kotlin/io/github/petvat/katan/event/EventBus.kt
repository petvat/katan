package io.github.petvat.katan.event

import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.petvat.katan.ui.ktx.view.View

/**
 * Loop-back
 * Event bus for incoming responses from server.
 */
object InEventBus {

    private val listeners = mutableListOf<EventListener>()


    fun fire(event: Event) {
        // TODO: This needs a lock!
        listeners.forEach {
            it.onEvent(event)
        }
    }

    operator fun plusAssign(listener: EventListener) {
        listeners += listener
    }

    // NOTE: EHH
    operator fun plusAssign(listenerGroup: Stage) {
        listenerGroup.actors
            .filterIsInstance<View<*>>()
            .forEach {
                this += it.viewModel
            }
    }

    operator fun minusAssign(listener: EventListener) {
        listeners -= listener
    }
}
