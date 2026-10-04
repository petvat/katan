package io.github.petvat.katan.event


/**
 * This event fires on successful login.
 */
data object LoginEvent : Event

/**
 * This event fires on a successful group creation.
 */
data object CreateEvent : Event

/**
 * This event fires when this user has joined a group.
 */
data object JoinEvent : Event

/**
 * This event fires on successful server connection.
 */
data object ConnectionEvent : Event


