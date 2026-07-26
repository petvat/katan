package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.viewmodel.ViewModel

/**
 * Scene2d view.
 */
abstract class KtxView<T : ViewModel>(
    skin: Skin,
    override val viewModel: T,
) : Table(skin), View<T>


/**
 * Any view.
 */
interface View<T : ViewModel> {
    val viewModel: T
    fun registerOnPropertyChanges()
}
