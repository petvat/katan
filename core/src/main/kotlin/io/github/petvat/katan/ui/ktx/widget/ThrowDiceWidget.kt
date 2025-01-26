package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.controller.RequestController
import ktx.actors.onChangeEvent
import ktx.scene2d.KTable
import ktx.scene2d.textButton

class ThrowDiceWidget(
    skin: Skin,
    controller: RequestController // HACK.
) : Table(skin), KTable {

    init {
        textButton("Throw dice") {
            onChangeEvent { controller.handleRollDice() }
        } // TODO: Replace with Image!

    }
}
