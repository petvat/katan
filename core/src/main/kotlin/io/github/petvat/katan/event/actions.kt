package io.github.petvat.katan.event

import io.github.petvat.katan.shared.model.board.BuildKind


/**
 * INTERNAL
 * This event fires if there is a request for
 */
data

class PlaceBuildingCommand<out K : BuildKind>(val buildKind: K) : Event
