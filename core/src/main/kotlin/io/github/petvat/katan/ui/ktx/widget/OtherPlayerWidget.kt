package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.model.PlayerColor
import ktx.scene2d.*


/**
 *
 */
class OtherPlayerWidget(
    displayName: String,
    color: PlayerColor,
    victoryPoints: Int,
    cardCount: Int,
    roads: Int? = null,
    villages: Int? = null,
    skin: Skin,
) : Table(skin), KTable {


    private val nameLabel = label(displayName, defaultStyle, skin) {
        it.top()
        it.left()
    }

    private val vpLabel = label("VP: $victoryPoints")

    private val cc = label("Cards: $cardCount")

    private val turnLabel = label("")

    init {
        debug = true
        nameLabel
        row()
        vpLabel
        cc // rename
        row()
        turnLabel
    }

    fun activateTurn() {
        // TODO: Change the background or something
        turnLabel.setText("TURN")
    }

    fun deactivateTurn() {
        turnLabel.setText("")
    }

    /**
     * Updates this player widget.
     */
    fun update(victoryPoints: Int? = null, cardCount: Int? = null, roads: Int? = null, villages: Int? = null) {
        victoryPoints?.let { vpLabel.setText(it) }
        cardCount?.let { cc.setText(it) }
        //roads?.let {  }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.otherPlayerStats(
    displayName: String,
    color: PlayerColor,
    victoryPoints: Int,
    cardCount: Int,
    roads: Int? = null,
    villages: Int? = null,
    skin: Skin,
    init: OtherPlayerWidget.(S) -> Unit = {}
): OtherPlayerWidget = actor(
    OtherPlayerWidget(
        displayName, color, victoryPoints, cardCount, roads, villages, skin
    ), init
)


