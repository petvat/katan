package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.*

class CreateGroupWidget(
    callback: () -> Unit,
    skin: Skin
) : Table(skin), KTable {

    val createGroup: Label
    val settings: Label
    val createBtn: TextButton

    init {
        // setFillParent(true)
        align(Align.center)
        createGroup = scene2d.label("Create group")
        settings = scene2d.label("Settings placeholder") {
            onClick {
                println("TODO: Settings not implemented!")
            }
        }
        createBtn = scene2d.textButton("Create") {
            onClick { callback() }
        }

        add(createGroup)
        row()
        add(settings).growX()
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



