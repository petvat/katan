package io.github.petvat.katan.event


import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface Event

interface EventListener {
    fun onEvent(event: Event)
}

/**
 * This event fires if there was error.
 */
data class ErrorEvent(val reason: String, val code: ErrorCode? = null) : Event

data object ConnectionLostEvent : Event

