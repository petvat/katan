package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.model.GroupViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors


class GroupScreen(game: KtxKatan) : AbstractScreen(game) {
    override fun buildStage() {
        stage.actors {
            GroupView(
                GroupViewModel(game.model.group!!, game.controller, game.transitionService),
                Scene2DSkin.defaultSkin
            )
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }
}

