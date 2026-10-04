package io.github.petvat.katan.event

/**
 * This event fires on receiving a new chat Event.
 */
data class ChatEvent(val from: String, val message: String) : Event


