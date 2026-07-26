package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.petvat.katan.ui.viewmodel.GameVM
import ktx.scene2d.*

/**
 */
class TradeView(
    viewModel: GameVM,
    skin: Skin
) : KtxView<GameVM>(skin, viewModel), KTable {

    init {
        label("TODO: Trade view.")
        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
    }
}


@Scene2dDsl
fun <S> KWidget<S>.tradeView(
    viewModel: GameVM,
    skin: Skin,
    init: (@Scene2dDsl TradeView).(S) -> Unit = {},
): TradeView = actor(TradeView(viewModel, skin), init)
