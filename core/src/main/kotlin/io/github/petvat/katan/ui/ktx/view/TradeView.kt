package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.petvat.katan.ui.model.GameViewModel
import ktx.scene2d.*

/**
 */
class TradeView(
    viewModel: GameViewModel,
    skin: Skin
) : View<GameViewModel>(skin, viewModel), KTable {

    init {
        label("TODO: Trade view.")
        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
    }
}


@Scene2dDsl
fun <S> KWidget<S>.tradeView(
    viewModel: GameViewModel,
    skin: Skin,
    init: (@Scene2dDsl TradeView).(S) -> Unit = {},
): TradeView = actor(TradeView(viewModel, skin), init)
