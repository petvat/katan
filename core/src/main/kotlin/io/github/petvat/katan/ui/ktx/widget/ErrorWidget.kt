package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.scene2d.*


fun createErrorWindow(skin: Skin, message: String): KWindow {
    return scene2d.window(title = "Error!") {
        table {
            align(Align.center)
            label("Message here.")
            textButton(message).onChange { remove() }
        }
    }
}


class ErrorWidget(

    skin: Skin
) : Table(skin), KTable {

    private val message = label("")
    private val exitBtn = textButton("OK") {
        onChangeEvent { this@ErrorWidget.hide() }
    }

    init {
        align(Align.center)
        message
        hide()
    }

    fun show(message: String) {
        this.message.setText(message)
        isVisible = true
    }

    private fun hide() {
        isVisible = false
    }
}


@Scene2dDsl
fun <S> KWidget<S>.error(
    skin: Skin,
    init: ErrorWidget.(S) -> Unit = {}
): ErrorWidget = actor(ErrorWidget(skin), init)


