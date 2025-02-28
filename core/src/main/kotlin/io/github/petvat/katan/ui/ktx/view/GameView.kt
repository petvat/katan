package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
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

    private val logger = KotlinLogging.logger { }

    private val rollDiceBtn: TextButton

    private val buildBtn: TextButton

    private val chat: ChatWidget

    private val buildWidget: BuildTable

    private val buildTable: Table

    // TODO: Use Player model?
    private val playersInfoWidget: OtherPlayersTable

    private val thisPlayerInfoTable: ThisPlayerTable

    init {
        setFillParent(true)
        align(Align.center)
        playersInfoWidget = scene2d.playersTable(viewModel.otherPlayersViewModelProperty, skin) {
            this.top()
            this.center()
        }
        chat = scene2d.chat(callback = viewModel::handleChat, skin = skin) {
            this.align(Align.bottomLeft)
            this.bottom()
            this.left()
            // padRight(50f)
        }
        buildWidget = scene2d.buildTable(skin, { cmd -> viewModel.onEvent(cmd) }) {
            isVisible = false
        }
        buildBtn = scene2d.textButton("Build") {
            onChangeEvent { this@GameView.buildWidget.isVisible = true }
            isDisabled = true
        }

        buildTable = scene2d.table {
            this.bottom()
        }

        thisPlayerInfoTable = scene2d.thisPlayerTable(
            this@GameView.viewModel.thisPlayerViewModelProperty,
            skin
        ) {
            this.bottom()
        }
        rollDiceBtn = scene2d.textButton("Roll dice") {
            onChangeEvent { this@GameView.viewModel.handleRollDice() }
        }

        buildTable.add(buildWidget)
        buildTable.row()
        buildTable.add(buildBtn)

        add(playersInfoWidget).colspan(4).growX().maxWidth(1000f)
        row().expand()
        add(chat).bottom()
        add(thisPlayerInfoTable).growX().bottom()
        add(buildTable).bottom()
        add(rollDiceBtn).bottom()

        registerOnPropertyChanges()
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

        viewModel.onPropertyChange(GameViewModel::lastGroupMessage) {
            logger.debug { "Reached game view property change!" }
            chat.addMessage(it.first, it.second)
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
            chat.addAll(it)
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

