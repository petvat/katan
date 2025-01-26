package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventListener
import io.github.petvat.katan.ui.model.GameViewModel
import io.github.petvat.katan.ui.model.View
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

/**
 * TODO: isVisible = false as default
 */
class TradeView(
    viewModel: GameViewModel,
    skin: Skin
) : View<GameViewModel>(skin, viewModel), KTable {
    override fun registerOnPropertyChanges() {
    }

    override fun onEvent(event: Event) {
        TODO("Not yet implemented")
    }

}


@Scene2dDsl
fun <S> KWidget<S>.tradeView(
    viewModel: GameViewModel,
    skin: Skin,
    init: (@Scene2dDsl TradeView).(S) -> Unit = {},
): TradeView = actor(TradeView(viewModel, skin), init)
