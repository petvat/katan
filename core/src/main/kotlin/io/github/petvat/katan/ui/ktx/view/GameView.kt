package io.github.petvat.core.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.ktx.widget.*
import io.github.petvat.katan.ui.model.GameViewModel
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
    skin: Skin
) : KTable, View<GameViewModel>(skin, viewModel) {

    private val rollDiceBtn: TextButton

    private val buildBtn: TextButton

    private val chat: ChatWidget

    private val buildTable: BuildTable

    // TODO: Use Player model?
    private val playersInfoWidget: OtherPlayersTable

    private val thisPlayerInfoTable: ThisPlayerTable

    init {
        setFillParent(true)
        align(Align.center)
        playersInfoWidget = playersTable(viewModel.otherPlayersViewModelProperty, skin) {
            it.top()
            it.center()
        }
        row()
        chat = chat(callback = { message: String -> viewModel.handleChat(message) }, skin = skin) {
            it.bottom()
            it.left()
            it.padRight(10f)
        }
        row()

        buildTable = buildTable(skin, { cmd -> viewModel.onEvent(cmd) }) {
            isVisible = false
        }

        buildBtn = textButton("Build") {
            onChangeEvent { this@GameView.buildTable.isVisible = true }
            isDisabled = true
        }

        thisPlayerInfoTable = thisPlayerTable(
            viewModel.thisPlayerViewModelProperty,
            skin
        )

        rollDiceBtn = textButton("Roll dice") {
            onChangeEvent { this@GameView.viewModel.handleRollDice() }
        }
    }


    private fun toggle(btn: Button, value: Boolean) {
        btn.isDisabled = !value
        btn.touchable = if (value) Touchable.enabled else Touchable.disabled
    }

    override fun registerOnPropertyChanges() {

        viewModel.onPropertyChange(GameViewModel::currentTurnPlayer) {
            if (viewModel.thisPlayerTurn) {
                thisPlayerInfoTable.activateTurn()
            } else {
                thisPlayerInfoTable.deactivateTurn()
                playersInfoWidget.activateTurn(it)
            }
        }

        viewModel.onPropertyChange(GameViewModel::buildModeProperty) {
            toggle(buildBtn, it)
        }

        viewModel.onPropertyChange(GameViewModel::rollDiceModeProperty) {
            toggle(rollDiceBtn, it)
        }

        viewModel.onPropertyChange(GameViewModel::diceRollProperty) {
            // TODO: Rolldice widget start
            // This should start an animation and display the dice roll.
        }

        viewModel.onPropertyChange(GameViewModel::chatLogProperty) {
            // chat.addMessage(it.last().first, it.last().second) TODO: USE
            chat.update(it)
        }

        viewModel.onPropertyChange(GameViewModel::thisPlayerViewModelProperty) {
            thisPlayerInfoTable.update(it.inventory, it.victoryPoints)
        }

        viewModel.onPropertyChange(GameViewModel::otherPlayersViewModelProperty) {
            it.forEach { player ->
                // TODO: Use function?
                playersInfoWidget.update(player.playerNumber, player.victoryPoints, player.cardCount)
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

