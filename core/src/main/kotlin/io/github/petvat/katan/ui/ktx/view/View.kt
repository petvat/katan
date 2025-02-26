package io.github.petvat.core.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.model.ViewModel

/**
 * Scene2d view.
 */
abstract class View<T : ViewModel>(
    skin: Skin,
    override val viewModel: T,
) : Table(skin), Graphic<T>


/**
 * Any view.
 */
interface Graphic<T : ViewModel> {
    val viewModel: T
    fun registerOnPropertyChanges()
}
