package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.viewmodel.ViewModel

/**
 * Scene2d view.
 */
abstract class KtxView(
    skin: Skin,
) : Table(skin), View


/**
 * Any view.
 */
interface View {
    fun registerOnPropertyChanges()
}
