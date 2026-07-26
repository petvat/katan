package io.github.petvat.katan.event

import io.github.petvat.katan.shared.model.board.BuildKind


sealed interface Command

/**
 * INTERNAL
 * This event fires if there is a request for
 *
 * TODO: Rename to Event or properly differentiate between commands and events.
 */
data class PlaceBuildingCommand<out K : BuildKind>(val buildKind: K) : Command
