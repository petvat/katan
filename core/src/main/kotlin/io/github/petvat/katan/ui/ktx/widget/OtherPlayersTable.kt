package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.OtherPlayerViewModel
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor


@Scene2dDsl
class OtherPlayersTable(
    otherPlayers: List<OtherPlayerViewModel>,
    skin: Skin
) : Table(skin), KGroup {


    private val otherPlayersWidget: Map<Int, OtherPlayerWidget>

    init {
        debug = true
        align(Align.center)

        otherPlayersWidget = otherPlayers.associate {
            it.playerNumber to otherPlayerStats(
                it.name,
                it.color,
                it.victoryPoints,
                it.cardCount,
                skin = skin
            ) {
                pad(5f)
            }
        }
    }

    fun activateTurn(playerNumber: Int) {
        otherPlayersWidget.forEach { (_, widget) ->
            widget.deactivateTurn() // Cheap solution for now.
        }
        otherPlayersWidget[playerNumber]?.activateTurn()
    }


    /**
     * Updates a player widget.
     */
    fun update(playerNumber: Int, vp: Int?, cardCount: Int?) {
        otherPlayersWidget[playerNumber]?.update(victoryPoints = vp, cardCount = cardCount)
    }

//    /**
//     * @param playerUpdates Key is player number, first is card count, second is VPs.
//     */
//    fun update(resourceUpdates: Map<Int, Int>?, vpUpdates: Map<Int, Int>?) {
//        resourceUpdates?.forEach { (playerNumber, upds) ->
//            otherPlayersWidget
//                .find { it.playerNumber == playerNumber }!!
//                .update(
//                    cardCount = upds
//                )
//        }
//        vpUpdates?.forEach { (playerNumber, upds) ->
//            otherPlayersWidget
//                .find { it.playerNumber == playerNumber }!!
//                .update(
//                    victoryPoints = upds
//                )
//        }
//    }
}

@Scene2dDsl
fun <S> KWidget<S>.playersTable(
    otherPlayers: List<OtherPlayerViewModel>,
    skin: Skin,
    init: OtherPlayersTable.(S) -> Unit = {}
): OtherPlayersTable = actor(OtherPlayersTable(otherPlayers, skin), init)




