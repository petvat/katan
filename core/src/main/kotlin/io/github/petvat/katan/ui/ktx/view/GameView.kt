package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.ui.ktx.widget.*
import io.github.petvat.katan.ui.projection.GameProjection
import io.github.petvat.katan.ui.viewmodel.GameVM
import io.github.petvat.katan.ui.viewmodel.GroupViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*

/**
 * The class represents the main ktxCtx view.
 *
 * @param viewModel The underlying [GameVM] that this view is tracking.
 * @param skin The [Skin] this widget should use.
 */
@Scene2dDsl
class GameView(
    gameProjection: GameProjection,
    viewModel: GameVM,
    skin: Skin
) : KTable, KtxView<GameVM>(skin, viewModel) {

    private val logger = KotlinLogging.logger { }

    private val rollDiceBtn: ImageButton

    private val buildBtn: TextButton

    private val chat: ChatWidget

    private val buildWidget: BuildTable

    private val buildTable: Table

    // TODO: Use Player gameState?
    private val playersInfoWidget: OtherPlayersTable

    private val thisPlayerInfoTable: ThisPlayerTable

    // private val diceAnimationWidget: DiceAnimationWidget = DiceAnimationWidget(skin)

    init {
        setFillParent(true)
        align(Align.center)
        touchable = Touchable.childrenOnly // Fix input processing touch event
        playersInfoWidget = scene2d.playersTable(gameProjection.otherPlayers, skin) {
            this.top()
            this.center()
        }
        chat = scene2d.chat(callback = viewModel::sendMessage, skin = skin) {
            this.align(Align.bottomLeft)
            this.bottom()
            this.left()
            // padRight(50f)
        }
        buildWidget = scene2d.buildTable(skin, { cmd -> viewModel.onBuildSelected(cmd.buildKind) }) {
            isVisible = false
        }
        buildBtn = scene2d.textButton("Build") {
            onChangeEvent { this@GameView.buildWidget.toggleActive() }
            isDisabled = true
        }

        buildTable = scene2d.table {
            this.bottom()
        }

        thisPlayerInfoTable = scene2d.thisPlayerTable(
            gameProjection.thisPlayer,
            skin
        ) {
            this.bottom()
        }

        val diceTex =
            TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("./dice-icon.png"))))

        diceTex.setMinSize(diceTex.minWidth * 2, diceTex.minHeight * 2)
        rollDiceBtn = ImageButton(diceTex)
        rollDiceBtn.onChangeEvent {
            this@GameView.viewModel.handleRollDice()
        }

        buildTable.add(buildWidget).growX()
        buildTable.row()
        buildTable.add(buildBtn)

        add(playersInfoWidget).colspan(4).growX().maxWidth(1000f)
        row().expandY()
        add(chat).bottom()
        add(thisPlayerInfoTable).expandX().bottom()
        add(buildTable).bottom().growX()
        add(rollDiceBtn).bottom()

        // add(diceAnimationWidget).bottom()

        registerOnPropertyChanges()
    }


    private fun toggle(btn: Button, value: Boolean) {
        btn.isDisabled = !value
        btn.touchable = if (value) Touchable.enabled else Touchable.disabled
    }


    override fun registerOnPropertyChanges() {
        logger.debug { "GameView:RegisterOnPropertyChanges" }

        viewModel.onPropertyChange(GameVM::projection) { projection ->
            renderPlayers(projection)
            logger.debug { "viewModel.onPropertyChanges" }
            // TODO: Add
            // renderTurn(projection.currentTurnPlayer)
        }

        viewModel.onPropertyChange(GroupViewModel::lastMessage) {
            chat.addMessage(it.first, it.second)

        }

        viewModel.onPropertyChange(GameVM::rollDiceMode) {
            toggle(rollDiceBtn, it)
        }

        viewModel.onPropertyChange(GameVM::buildMode) {
            toggle(buildBtn, it)
            logger.debug { "Toggled build mode: $it" }
        }

        viewModel.onPropertyChange(GameVM::diceRoll) { roll ->
//            roll?.let { (d1, d2) ->
//                diceAnimationWidget.playRoll(d1, d2) {
//                    viewModel.onDiceAnimationComplete()
//                }
//            }
        }
    }

    private fun renderPlayers(projection: GameProjection) {
        thisPlayerInfoTable.update(projection.thisPlayer.inventory, projection.thisPlayer.victoryPoints)
        projection.otherPlayers.forEach {
            playersInfoWidget.update(it.playerNumber, it.victoryPoints, it.cardCount)
        }
    }

    private fun renderTurn(projection: GameProjection) {
        // Render glow effect or something?
    }


//
//    override fun registerOnPropertyChanges() {
//
//        viewModel.onPropertyChange(GameViewModel::currentTurnPlayer) {
//            if (viewModel.thisPlayerTurn) {
//                thisPlayerInfoTable.activateTurn()
////                if (!viewModel.setupPhase) {
////                    toggle(rollDiceBtn, true)
////                }
//            } else {
//                thisPlayerInfoTable.deactivateTurn() // redundant.
//                playersInfoWidget.activateTurn(it)
//            }
//        }
//
//        viewModel.onPropertyChange(GameViewModel::lastGroupMessage) {
//            logger.debug { "Reached ktxCtx view property change!" }
//            chat.addMessage(it.first, it.second)
//        }
//
//        viewModel.onPropertyChange(GameViewModel::buildMode) {
//            toggle(buildBtn, it)
//        }
//
//        viewModel.onPropertyChange(GameViewModel::rollDiceMode) {
//            toggle(rollDiceBtn, it)
//        }
//
//        viewModel.onPropertyChange(GameViewModel::diceRoll) {
//            // TODO: Rolldice widget start
//            // This should start an animation and display the dice roll.
//        }
//
//        viewModel.onPropertyChange(GameViewModel::chatLogProperty) {
//            // chat.addMessage(it.last().first, it.last().second) TODO: USE
//            chat.addAll(it)
//        }
//
//        viewModel.onPropertyChange(GameViewModel::thisPlayerVM) {
//            thisPlayerInfoTable.update(it.inventory, it.victoryPoints)
//        }
//
//        viewModel.onPropertyChange(GameViewModel::otherPlayersVM) {
//            it.forEach { player ->
//                // TODO: Use function?
//                playersInfoWidget.update(player.playerNumber, player.victoryPoints, player.cardCount)
//            }
//        }
//    }
}


@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameVM,
    projection: GameProjection,
    skin: Skin,
    init: (@Scene2dDsl GameView).(S) -> Unit = {},
): GameView = actor(GameView(projection, model, skin), init)

