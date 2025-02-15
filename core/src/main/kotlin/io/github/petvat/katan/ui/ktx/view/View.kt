package io.github.petvat.katan.ui.model

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.event.EventListener

/**
 * NOTE: Could also use a reactive viewmodel -> views.upd
 */


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
