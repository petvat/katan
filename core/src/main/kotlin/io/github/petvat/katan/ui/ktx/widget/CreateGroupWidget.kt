package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.scene2d.*

class CreateGroupWidget(
    callback: () -> Unit,
    skin: Skin
) : Table(skin), KTable {

    val createGroup: Label
    val settings: ScrollPaneWidget<SettingsWidget>
    val createBtn: TextButton

    init {
        // setFillParent(true)
        align(Align.center)
        createGroup = scene2d.label("Create group") {
            setFontScale(1.5f)
            setAlignment(Align.center)
        }
        settings = scene2d.scrollWidget {

        }

        repeat(5) {
            settings.add(scene2d.settingsWidget("Stat $it", {}, skin))
        }

        createBtn = scene2d.textButton("Create") {
            onClick { callback() }
        }

        add(createGroup).top()
        row()
        add(settings).grow().padTop(10f)
        row()
        add(createBtn)
    }
}

@Scene2dDsl
fun <S> KWidget<S>.createWidget(
    callback: () -> Unit,
    skin: Skin,
    init: CreateGroupWidget.(S) -> Unit = {}
): CreateGroupWidget = actor(CreateGroupWidget(callback, skin), init)



