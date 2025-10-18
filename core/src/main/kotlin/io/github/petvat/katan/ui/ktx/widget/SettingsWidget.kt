package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.*


class SettingsWidget(desc: String, skin: Skin) : Table(skin), KTable {

    init {
        background = skin.getDrawable("slot")

        val descLabel = scene2d.label(desc)
        add(descLabel).growX()
    }
}

@Scene2dDsl
fun <S> KWidget<S>.settingsWidget(
    desc: String,
    callback: () -> Unit,
    skin: Skin,
    init: SettingsWidget.(S) -> Unit = {}
): SettingsWidget = actor(SettingsWidget(desc, skin), init)


