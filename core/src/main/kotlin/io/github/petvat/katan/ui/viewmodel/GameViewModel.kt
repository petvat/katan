package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.command.KatanCommands
import io.github.petvat.katan.model.state.ChatSession
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.ui.projection.Projector

/**
 * This view model is the UI part of the game.
 */
class GameViewModel(
    private val state: ClientState,
    private val commands: KatanCommands,
    private val boardViewModel: BoardViewModel, // TODO: Not great
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    val chat by mirror(
        { state.chat },
        requireNotNull(state.chat) { "GameScreen built before init." },
        { Projector.project(it) }
    )

    val hud by mirror(
        { state.game },
        requireNotNull(state.game) { "GameScreen built before init." },
        { Projector.project(it) },
        gate = { !diceAnimationRunning }
    )

    var diceRoll by propertyNotify<Pair<Int, Int>?>(null)
        private set

    /** Theater flag: freezes [hud] while the dice animation plays. */
    private var diceAnimationRunning = false

    fun handleRollDice() = commands.game.rollDice()
    fun sendMessage(message: String) = commands.chat.send(message)
    fun onBuildSelected(build: BuildTarget) = boardViewModel.selectPlacingTarget(build)

    override fun onEvent(event: Event) {
        when (event) {
            is RolledDiceEvent -> {
                logger.debug { "$event" }
                diceAnimationRunning = true // close gate: HUD frozen at pre-roll values
                diceRoll = event.roll1 to event.roll2
            }

            else -> {
                logger.debug { "$event" }
            }
        }
    }

    /**
     * Called by GameView once the dice animation lands.
     */
    fun onDiceAnimationComplete() {
        diceAnimationRunning = false // gate opens; next refresh() flushes the HUD
        diceRoll = null // reset so an identical future roll still notifies
    }
}
//
//class GameVM(
//    val gameState: GameState,
//    val chatState: ChatState,
//    private val gameActions: IGameActions,
//    private val chatActions: IChatActions
//) : ViewModel() {
//    private val logger = KotlinLogging.logger { }
//
//    var projection by propertyNotify(Projector.project(state))
//        private set
//
//    var initSettlementPlacingMode by propertyNotify(false)
//    var initRoadSettlementPlacingMode by propertyNotify(false)
//
//    var roadPlacingMode by propertyNotify(false)
//    var settlementPlacingMode by propertyNotify(false)
//    var cityPlacingMode by propertyNotify(false)
//
//    var rollDiceMode by propertyNotify(false)
//    var buildMode by propertyNotify(false)
//    var diceRoll by propertyNotify<Pair<Int, Int>?>(null)
//
//    private var pendingAnimationCallback: (() -> Unit)? = null
//
//    fun handleRollDice() = gameActions.rollDice()
//
//    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) = gameActions.build(buildKind, coordinates)
//
//    fun sendMessage(message: String) = chatActions.sendMessage(message)
//
//    fun onBuildSelected(buildKind: BuildKind) {
//        when (buildKind) {
//            is BuildKind.Village -> when (buildKind.kind) {
//                VillageKind.SETTLEMENT -> {
//                    settlementPlacingMode = true
//                    logger.debug { "OnBuildSelected:VillageKind.SETTLEMENT" }
//
//                }
//
//                VillageKind.CITY -> cityPlacingMode = true
//            }
//
//            is BuildKind.Road -> roadPlacingMode = true
//        }
//    }
//
//
//    fun onBoardTap(coordinates: Coordinates) {
//        when {
//            initRoadSettlementPlacingMode -> {
//                gameActions.build(BuildKind.Village(VillageKind.SETTLEMENT), coordinates)
//                settlementPlacingMode = false  // exit placement mode immediately after tap
//            }
//
//            settlementPlacingMode -> {
//                gameActions.build(BuildKind.Village(VillageKind.SETTLEMENT), coordinates)
//                settlementPlacingMode = false  // exit placement mode immediately after tap
//            }
//
//            cityPlacingMode -> {
//                gameActions.build(BuildKind.Village(VillageKind.CITY), coordinates)
//                cityPlacingMode = false
//            }
//
//            roadPlacingMode -> {
//                gameActions.build(BuildKind.Road(RoadKind.ROAD), coordinates)
//                roadPlacingMode = false
//            }
//        }
//    }
//
//    override fun onEvent(event: Event) {
//        when (event) {
//            is MyTurnSetupEvent -> {
//                // TODO: animation
//                settlementPlacingMode = true
//            }
//
//            is RolledDiceEvent -> {
//                diceRoll = event.roll1 to event.roll2
//                pendingAnimationCallback = {
//                    refreshProjection()
//                    rollDiceMode = false
//                    buildMode = state.turnPlayer == state.player
//                }
//            }
//
//            is BuildEvent -> {
//                refreshProjection()
//            }
//
//            is MyTurnEvent -> {
//                rollDiceMode = true
//                refreshProjection()
//            }
//
//            is NextTurnEvent -> {
//                refreshProjection()
//            }
//
//            else -> Unit
//        }
//    }
//
//    /** Called by GameView once the dice animation actually finishes playing. */
//    fun onDiceAnimationComplete() {
//        pendingAnimationCallback?.invoke()
//        pendingAnimationCallback = null
//        diceRoll = null // reset so a future identical roll (e.g. 3,4 again) still triggers propertyNotify
//    }
//
//    private fun refreshProjection() {
//        projection = Projector.project(state)
//    }
//}
