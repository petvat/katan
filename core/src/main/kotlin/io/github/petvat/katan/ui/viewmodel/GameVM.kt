package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.ChatActions
import io.github.petvat.katan.controller.GameActions
import io.github.petvat.katan.controller.IChatActions
import io.github.petvat.katan.controller.IGameActions
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.ChatState
import io.github.petvat.katan.model.GameState
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.ui.projection.GameProjector


class GameVM(val state: GameState, private val gameActions: IGameActions, private val chatActions: IChatActions) :
    ViewModel() {
    private val logger = KotlinLogging.logger { }

    var projection by propertyNotify(GameProjector.project(state))
        private set

    var initSettlementPlacingMode by propertyNotify(false)
    var initRoadSettlementPlacingMode by propertyNotify(false)

    var roadPlacingMode by propertyNotify(false)
    var settlementPlacingMode by propertyNotify(false)
    var cityPlacingMode by propertyNotify(false)

    var rollDiceMode by propertyNotify(false)
    var buildMode by propertyNotify(false)
    var diceRoll by propertyNotify<Pair<Int, Int>?>(null)

    private var pendingAnimationCallback: (() -> Unit)? = null

    fun handleRollDice() = gameActions.rollDice()

    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) = gameActions.build(buildKind, coordinates)

    fun sendMessage(message: String) = chatActions.sendMessage(message)

    fun onBuildSelected(buildKind: BuildKind) {
        when (buildKind) {
            is BuildKind.Village -> when (buildKind.kind) {
                VillageKind.SETTLEMENT -> {
                    settlementPlacingMode = true
                    logger.debug { "OnBuildSelected:VillageKind.SETTLEMENT" }

                }

                VillageKind.CITY -> cityPlacingMode = true
            }

            is BuildKind.Road -> roadPlacingMode = true
        }
    }

    fun onBoardTap(coordinates: Coordinates) {
        when {
            initRoadSettlementPlacingMode -> {
                gameActions.build(BuildKind.Village(VillageKind.SETTLEMENT), coordinates)
                settlementPlacingMode = false  // exit placement mode immediately after tap
            }

            settlementPlacingMode -> {
                gameActions.build(BuildKind.Village(VillageKind.SETTLEMENT), coordinates)
                settlementPlacingMode = false  // exit placement mode immediately after tap
            }

            cityPlacingMode -> {
                gameActions.build(BuildKind.Village(VillageKind.CITY), coordinates)
                cityPlacingMode = false
            }

            roadPlacingMode -> {
                gameActions.build(BuildKind.Road(RoadKind.ROAD), coordinates)
                roadPlacingMode = false
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is MyTurnSetupEvent -> {
                // TODO: animation
                settlementPlacingMode = true
            }

            is RolledDiceEvent -> {
                diceRoll = event.roll1 to event.roll2
                pendingAnimationCallback = {
                    refreshProjection()
                    rollDiceMode = false
                    buildMode = state.turnPlayer == state.player
                }
            }

            is BuildEvent -> {
                refreshProjection()
            }

            is MyTurnEvent -> {
                rollDiceMode = true
                refreshProjection()
            }

            is NextTurnEvent -> {
                refreshProjection()
            }

            else -> Unit
        }
    }

    /** Called by GameView once the dice animation actually finishes playing. */
    fun onDiceAnimationComplete() {
        pendingAnimationCallback?.invoke()
        pendingAnimationCallback = null
        diceRoll = null // reset so a future identical roll (e.g. 3,4 again) still triggers propertyNotify
    }

    private fun refreshProjection() {
        projection = GameProjector.project(state)
    }
}
