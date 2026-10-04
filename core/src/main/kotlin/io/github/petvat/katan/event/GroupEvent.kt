package io.github.petvat.katan.event

import io.github.petvat.katan.model.state.GroupExternal


/**
 * This event fires when a group has started a game.
 */
data object InitGameEvent : Event

/**
 * This event fires when this user has left the group.
 */
data object LeaveEvent : Event

/**
 * This event fires when a user has joined the group.
 */
data object UserJoinedEvent : Event

data object UserLeftEvent : Event

data class GroupUpdateEvent(
    val groupSummary: GroupExternal
) : Event

