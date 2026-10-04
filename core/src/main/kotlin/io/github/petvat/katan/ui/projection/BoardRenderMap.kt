package io.github.petvat.katan.ui.projection

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.hexlib.PCoord
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.ui.ASSETS
import io.github.petvat.katan.ui.RoadOrientation


/**
 * STATIC render scaffold: everything pixel-related that never changes
 * after the board is generated. Compute once per game.
 */
data class BoardRenderMap(
    val tiles: Map<PCoord, ASSETS.Board>,      // island + shore + open sea
    val tokens: Map<PCoord, Int>,               // number tokens
    val hexes: Map<HexCoord, PCoord>,
    val nodes: Map<NodeCoord, PCoord>,
    val edges: Map<EdgeCoord, PCoord>
)

/**
 * DYNAMIC overlay: everything that changes with game state or placement mode.
 * Recomputed on every state change.
 */
data class BoardOverlay(
    val roads: Map<PCoord, RoadMarker>,
    val villages: Map<PCoord, VillageMarker>,
    val robber: PCoord,
    val frontier: Map<Coordinates, PCoord>
) {
    data class RoadMarker(val color: PlayerColor, val orientation: RoadOrientation)
    data class VillageMarker(val kind: VillageKind, val color: PlayerColor)
}
