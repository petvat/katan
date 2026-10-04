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
import io.github.petvat.katan.ui.viewmodel.GameViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*

/**
 * The class represents the main ktxCtx view.
 *
 * @param viewModel The underlying [GameViewModel] that this view is tracking.
 * @param skin The [Skin] this widget should use.
 */
@Scene2dDsl
class GameView(
    hud: GameProjection,
    val viewModel: GameViewModel,
    skin: Skin
) : KTable, KtxView(skin) {

    private val logger = KotlinLogging.logger { }

    private val rollDiceBtn: ImageButton

    private val buildBtn: TextButton

    private val chatWidget: ChatWidget

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
        playersInfoWidget = scene2d.playersTable(hud.otherPlayers, skin) {
            this.top()
            this.center()
        }
        chatWidget = scene2d.chat(callback = viewModel::sendMessage, skin = skin) {
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
            hud.thisPlayer,
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
        add(chatWidget).bottom()
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

        viewModel.onPropertyChange(GameViewModel::hud) {
            renderPlayers(it)
            renderTurn(it)
        }

        viewModel.onPropertyChange(GameViewModel::chat) {
            chatWidget.addAll(it.chatlog)
        }

        viewModel.onPropertyChange(GameViewModel::rollDiceMode) {
            toggle(rollDiceBtn, it)
        }

        viewModel.onPropertyChange(GameViewModel::buildMode) {
            toggle(buildBtn, it)
            logger.debug { "Toggled build mode: $it" }
        }

        viewModel.onPropertyChange(GameViewModel::diceRoll) { roll ->
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
}


@Scene2dDsl
fun <S> KWidget<S>.gameView(
    vm: GameViewModel,
    hud: GameProjection,
    skin: Skin,
    init: (@Scene2dDsl GameView).(S) -> Unit = {},
): GameView = actor(GameView(hud, vm, skin), init)

