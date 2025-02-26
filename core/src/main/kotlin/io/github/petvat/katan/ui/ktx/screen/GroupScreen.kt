package io.github.petvat.core.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.ktx.view.View
import io.github.petvat.katan.ui.model.GroupViewModel
import io.github.petvat.katan.ui.model.ViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors


class GroupScreen(game: KtxKatan) : AbstractScreen(game) {

    // NOTE: Or maybe init here. But that might not persist state?
    override lateinit var viewModel: GroupViewModel

    override fun show() {
        super.show()
        EventBus += this // To listen for errors.
        EventBus += viewModel
    }

    override fun buildStage() {

        viewModel = GroupViewModel(game.model.group, game.controller, game.transitionService)
        stage.actors {
            stage.addActor(
                GroupView(
                    viewModel,
                    Scene2DSkin.defaultSkin
                )
            )
        }
    }
}

