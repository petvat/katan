package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.scene2d.*


fun createErrorWindow(skin: Skin, message: String): KWindow {
    return scene2d.window(title = "Error!") {
        setFillParent(true)
        align(Align.center)
        add(scene2d.error(message, skin) { show(message) })
    }
}


class ErrorWidget(
    reason: String,
    skin: Skin
) : Table(skin), KTable {

    private val message: Label
    private val exitBtn: TextButton

    init {
        align(Align.center)
        setFillParent(true)

        val innertlb = scene2d.table {
            background = skin.getDrawable("area")
            this@ErrorWidget.message = scene2d.label(reason) {
                wrap = true
                setAlignment(Align.center)
            }
            this@ErrorWidget.exitBtn = scene2d.textButton("OK") {
                align(Align.bottom)
                onChangeEvent { this@ErrorWidget.remove() }
            }
        }

        innertlb.add(scene2d.label("Error!") {
            setFontScale(2f)
        })
        innertlb.row()
        innertlb.add(message).grow()
        innertlb.row().space(50f)
        innertlb.add(exitBtn).grow()
        add(innertlb).minWidth(300f).minHeight(100f).maxWidth(800f).maxHeight(800f)
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
    reason: String,
    skin: Skin,
    init: ErrorWidget.(S) -> Unit = {}
): ErrorWidget = actor(ErrorWidget(reason, skin), init)


