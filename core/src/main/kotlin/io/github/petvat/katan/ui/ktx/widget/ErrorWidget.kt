package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.scene2d.*


fun createErrorWindow(skin: Skin, message: String): KWindow {
    return scene2d.window(title = "Error!") {
        setFillParent(true)
        align(Align.center)
        scene2d.error(skin) { show(message) }
    }
}


class ErrorWidget(

    skin: Skin
) : Table(skin), KTable {

    private val message: Label
    private val exitBtn: TextButton

    init {
        setFillParent(true)
        align(Align.center)
        message = label("")
        row()
        exitBtn = textButton("OK") {
            onChangeEvent { this@ErrorWidget.remove() }
        }
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


