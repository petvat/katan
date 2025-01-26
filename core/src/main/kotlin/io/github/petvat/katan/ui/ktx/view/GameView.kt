package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.Game
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.*
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.model.GameViewModel
import io.github.petvat.katan.ui.ktx.widget.buildTable
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.ktx.widget.playersInfo
import io.github.petvat.katan.ui.ktx.widget.thisPlayerInfo
import io.github.petvat.katan.ui.model.View
import ktx.actors.onChangeEvent
import ktx.scene2d.*

/**
 * The class represents the main game view.
 *
 * @param viewModel The underlying [GameViewModel] that this view is tracking.
 * @skin The [Skin] this widget should use.
 */
@Scene2dDsl
class GameView(
    viewModel: GameViewModel,
    val skin: Skin
) : KTable, View<GameViewModel>(skin, viewModel) {

    private val rollDiceBtn = textButton("Roll dice") {
        onChangeEvent { this@GameView.viewModel.handleRollDice() }
    }

    private val buildBtn = textButton("Build") {
        onChangeEvent { this@GameView.buildTable.isVisible = true }
        isDisabled = true
    }

    private val chat = chat(skin = skin) {
        it.bottom()
        it.left()
        it.padRight(10f)
    }

    private val buildTable = buildTable(skin, { cmd -> viewModel.onEvent(cmd) }) {
        isVisible = false
    }

    // TODO: Use Player model?
    private val playersInfoWidget = playersInfo(viewModel.otherPlayersModel, skin) {
        it.top()
        it.center()
    }

    private val thisPlayerInfoTable = thisPlayerInfo(
        viewModel.thisPlayer.inventory,
        viewModel.thisPlayer.victoryPoints,
        skin
    )

    init {
        align(Align.center)
        playersInfoWidget
        row()
        row()
        chat

        buildBtn
        buildTable

        thisPlayerInfoTable

        rollDiceBtn
    }


    fun onUiEvent(event: UiEvent) {

    }

    // On property change instead.
//    override fun onEvent(event: Event) {
//        when (event) {
//            is ChatEvent -> {
//                chat.update(this@GameView.viewModel.chatLog)
//            }
//
//            is NextTurnEvent -> {
//                // TODO: player
//            } // Infer player, then move graphic
//            is BuildEvent -> {
//                updateVPs(
//                    viewModel.thisPlayer.victoryPoints,
//                    viewModel.otherPlayers.associate { it.playerNumber to it.victoryPoints }
//                )
//            }
//
//            is RolledDiceEvent -> {
//                updateResources(
//                    viewModel.thisPlayer.inventory,
//                    viewModel.otherPlayers.associate { it.playerNumber to it.cardCount }
//                )
//                // TODO: Current player
//
//                // More of a model thing.
//                if (viewModel.currentTurnPlayer == viewModel.thisPlayer.playerNumber) {
//                    deactiveRollButton()
//                }
//            }
//
//            is TurnStartEvent -> {
//                if (!viewModel.setupPhase) {
//                    activateRollButton()
//                }
//            }
//
//            else -> Unit
//        }
//    }


    private fun toggle(btn: Button, value: Boolean) {
        btn.isDisabled = !value
        btn.touchable = if (value) Touchable.enabled else Touchable.disabled
    }


    override fun onEvent(event: Event) {

    }

    /**
     * Update player resources display.
     */
//    private fun updateResources(
//        playerInventory: ResourceMap,
//        otherPlayersCardCounts: Map<Int, Int>,
//    ) {
//        otherPlayersCardCounts.forEach { (pnum, cc) ->
//            playersInfoWidget.updatePlayerStats(
//                pnum,
//                vp = null,
//                cardCount = cc
//            )
//        }
//        thisPlayerInfoTable.update(
//            playerInventory,
//            null
//        )
//    }

//    private fun updateVPs(
//        thisPlayerVPs: Int,
//        otherPlayersVPs: Map<Int, Int>
//    ) {
//        thisPlayerInfoTable.update(
//            null,
//            thisPlayerVPs
//        )
//
//        otherPlayersVPs.forEach { (pnum, vp) ->
//            playersInfoWidget.updatePlayerStats(pnum, vp = vp, null)
//        }
//
//    }

    override fun registerOnPropertyChanges() {

        viewModel.onPropertyChange(GameViewModel::buildMode) {
            toggle(buildBtn, it)
        }

        viewModel.onPropertyChange(GameViewModel::rollDiceMode) {
            toggle(rollDiceBtn, it)
        }

        viewModel.onPropertyChange(GameViewModel::chatLog) {
            chat.addMessage(it.last().first, it.last().second)
        }

        viewModel.onPropertyChange(GameViewModel::thisPlayerModel) {
            thisPlayerInfoTable.update(it.inventory, it.victoryPoints)
        }

        viewModel.onPropertyChange(GameViewModel::otherPlayersModel) {
            it.forEach { player ->
                playersInfoWidget.updatePlayerStats(player.playerNumber, player.victoryPoints, player.cardCount)
            }
        }
    }
}


@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameViewModel,
    skin: Skin,
    init: (@Scene2dDsl GameView).(S) -> Unit = {},
): GameView = actor(GameView(model, skin), init)

