package io.github.petvat.katan.ui.viewmodel

import com.badlogic.gdx.Gdx
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.model.BoardHelpers
import io.github.petvat.katan.model.command.GameCommands
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.ui.RoadOrientation
import io.github.petvat.katan.ui.ktx.BoardRenderUtils
import io.github.petvat.katan.ui.projection.BoardOverlay
import io.github.petvat.katan.ui.projection.BoardProjection
import io.github.petvat.katan.ui.projection.BoardProjector
import io.github.petvat.katan.ui.projection.BoardRenderMap
import io.github.petvat.katan.ui.roadOrientation

class BoardViewModel(
    private val state: ClientState,
    private val commands: GameCommands,
    layout: Layout,
) : ViewModel() {
    val projector: BoardProjector =
        BoardProjector(BoardHelpers(ruleBook = requireNotNull(state.game?.rules) { "Game init without rules." }))

    val colors = requireNotNull(state.game?.colors) { "Game init without colors." }
    val myColor = requireNotNull(colors[state.game!!.player]) {
        "No color assigned for player ${state.game!!.player}; colors=$colors"
    }

    val scaffold: BoardRenderMap =
        BoardRenderUtils.scaffold(layout, state.game!!.board)

    val board by mirror(
        { state.game?.board },
        requireNotNull(state.game?.board) { "GameScreen built before init." },
        { projector.project(it, requireNotNull(state.game).player) }
    )

    var overlay by propertyNotify(computeOverlay(board, null))


    var placingTarget: BuildTarget? by propertyNotify(null)
        private set

    var buildPlacingMode: Boolean by propertyNotify(false)

    init {
        onPropertyChange(::board) { overlay = computeOverlay(it) }
    }

    fun selectPlacingTarget(target: BuildTarget) {
        placingTarget = target
        overlay = computeOverlay(board)
    }

    fun cancelPlacing() {
        placingTarget = null
        overlay = computeOverlay(board)
    }

    fun onBoardTap(coordinates: Coordinates) {
        val target = placingTarget ?: return
        val kind = when (target) {
            BuildTarget.SETTLEMENT -> BuildKind.Village(VillageKind.SETTLEMENT)
            BuildTarget.CITY -> BuildKind.Village(VillageKind.CITY)
            BuildTarget.ROAD -> BuildKind.Road(RoadKind.ROAD)
        }
        commands.build(kind, coordinates)
        cancelPlacing()
    }

    private fun isSetupTurn(): Boolean {
        val game = requireNotNull(state.game)
        return game.phase == Phase.SETUP && game.turnPlayer == game.player
    }

    private fun computeOverlay(p: BoardProjection, target: BuildTarget? = placingTarget): BoardOverlay {
        val colors = requireNotNull(state.game?.colors)
        return BoardOverlay(
            // FIXME: Find the road orientation by looking at the coordiantes
            roads = p.paths.associate {
                scaffold.edges[it.coordinate]!! to BoardOverlay.RoadMarker(
                    colors.getValue(it.road.owner),
                    it.coordinate.roadOrientation()
                )
            },
            villages = p.intersections.associate {
                scaffold.nodes[it.coordinate]!! to
                    BoardOverlay.VillageMarker(it.village.villageKind, colors.getValue(it.village.owner))
            },
            robber = scaffold.hexes.getValue(p.robberLocation),
            frontier = frontierFor(target).associateWith { coord ->
                when (coord) {
                    is NodeCoord -> scaffold.nodes.getValue(coord)
                    is EdgeCoord -> scaffold.edges.getValue(coord)
                    else -> error("Unsupported coordinate type in frontier: $coord")
                }
            }
        )
    }

    /** Which frontier to highlight, depending on what the user is placing. */
    private fun frontierFor(target: BuildTarget?): List<Coordinates> = when (target) {
        BuildTarget.SETTLEMENT -> if (isSetupTurn()) board.setupFrontier else board.settlementFrontier
        BuildTarget.CITY -> board.cityFrontier
        BuildTarget.ROAD -> board.roadFrontier
        null -> emptyList()
    }

    override fun onEvent(event: Event) {
    }
}


//
//class BoardVM(
//    private val session: GameSession,
//    private val gameActions: IGameActions,
//    private val boardRenderingUtils: BoardRenderingUtils,
//    layout: Layout
//) : ViewModel<GameEvent>() {
//
//
//    // TODO: ONLY LOGICAL COORDS HERE. We can have a map in the view.
//    //  ADD BoardRenderMap -> instead of Positions, with all the coords we want to actually render.
//
//
//    private val nodes: Map<NodeCoord, PCoord>
//    private val edges: Map<EdgeCoord, PCoord>
//    private val hexes: Map<HexCoord, PCoord>
//    private val tiles: Map<PCoord, ASSETS.Board>
//
//
//    private var boardProjection = BoardProjector.project(session.state.board, session.state.player)
//
//    var positions: BoardPositions by propertyNotify(computePositions())
//        private set
//
//    var placingTarget: BuildTarget? by propertyNotify(null)
//
//
//    init {
//        val hexCoords = session.state.board.tiles.map { it.hexCoordinate }
//        nodes = BoardRenderUtils.mapNodes(layout, hexCoords)
//        edges = BoardRenderUtils.mapEdges(layout, hexCoords)
//        tiles = boardRenderingUtils.mapCompleteIsland()
//
//        session.onPropertyChange(GameSession::state) { newState ->
//            boardProjection = BoardProjector.project(newState.board, newState.player)
//
//        }
//    }
//
//    fun selectPlacingTarget(target: BuildTarget) {
//        placingTarget = target
//        positions = computePositions()
//    }
//
//    fun cancelPlacing() {
//        placingTarget = null
//        positions = computePositions()
//    }
//
//
//    override fun onEvent(event: GameEvent) {
//        TODO("Not yet implemented")
//    }
//
//
//    fun onBoardTap(coordinates: Coordinates) {
//        val target = placingTarget ?: return
//        val kind = when (target) {
//            BuildTarget.SETTLEMENT -> BuildKind.Village(VillageKind.SETTLEMENT)
//            BuildTarget.CITY -> BuildKind.Village(VillageKind.CITY)
//            BuildTarget.ROAD -> BuildKind.Road(RoadKind.ROAD)
//        }
//        gameActions.build(kind, coordinates)
//        cancelPlacing()
//    }
//
//    private fun isSetupTurn(state: GameState) =
//        state.phase == Phase.SETUP && state.turnPlayer == state.player
//
//
//    private fun computePositions(): BoardPositions {
//        val colors = session.state.colors
//        return BoardPositions(
//            tiles = boardProjection.tiles.associate { tiles[it.hexCoordinate]!! to it.resource!! },
//            tokens = boardProjection.tiles.filter { it.rollListenValue > 0 }
//                .associate { tiles[it.hexCoordinate]!! to it.rollListenValue },
//            roads = boardProjection.paths.associate {
//                edges[it.coordinate]!! to colors[it.road.owner]!!
//            },
//            villages = boardProjection.intersections.associate {
//                nodes[it.coordinate]!! to VillageMarker(it.village.villageKind, colors.getValue(it.village.owner))
//            },
//            robber = tiles[boardProjection.robberLocation]!!,
//            activeFrontier = activeFrontierFor(placingTarget, isSetupTurn(session.state))
//        )
//    }
//
//    private fun activeFrontierFor(target: BuildTarget?, isSetup: Boolean): Map<out Coordinates, PCoord> {
//        val coords: List<Coordinates> = when (target) {
//            BuildTarget.SETTLEMENT -> if (isSetup) boardProjection.setupFrontier else boardProjection.settlementFrontier
//            BuildTarget.CITY -> boardProjection.cityFrontier
//            BuildTarget.ROAD -> boardProjection.roadFrontier
//            null -> emptyList()
//        }
//        return coords.associateWith {
//            when (it) {
//                is NodeCoord -> nodes[it]!!
//                is EdgeCoord -> edges[it]!!
//                else -> error("Unsupported coordinate type in frontier: $it")
//            }
//        }
//    }
//
//    companion object {
//        enum class BuildTarget { SETTLEMENT, CITY, ROAD }
//        data class VillageMarker(val kind: VillageKind, val color: PlayerColor)
//        data class BoardPositions(
//            val tiles: Map<PCoord, Resource>,
//            val tokens: Map<PCoord, Int>,
//            val roads: Map<PCoord, PlayerColor>,
//            val villages: Map<PCoord, VillageMarker>,
//            val robber: PCoord,
//            val activeFrontier: Map<out Coordinates, PCoord>
//        )
//    }
//
//}

//
//class BoardViewModel(
//    state: GameState,
//    private val gameActions: IGameActions
//) : ViewModel() {
//
//    var boardProjection by propertyNotify(Projector.projectBoard(state.board, state.player))
//        private set
//
//    private var lastBoard = state.board
//
//    init {
//        session.onPropertyChange(GameSession::state) { newState ->
//            if (newState.board !== lastBoard) {
//                lastBoard = newState.board
//                boardProjection = Projector.projectBoard(newState.board, newState.player)
//            }
//        }
//    }
//
//    var settlementPlacingMode by propertyNotify(false)
//    var cityPlacingMode by propertyNotify(false)
//    var roadPlacingMode by propertyNotify(false)
//
//    fun onBoardTap(coordinates: Coordinates) { /* unchanged */
//    }
//
//    override fun onEvent(event: Event) {
//        when ()
//    }
//}
