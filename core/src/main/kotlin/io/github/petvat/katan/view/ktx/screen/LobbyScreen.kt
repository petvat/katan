package io.github.petvat.katan.view.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import io.github.petvat.katan.view.ktx.KtxKatan
import io.github.petvat.katan.view.ktx.ui.LobbyView
import ktx.log.Logger
import ktx.log.logger
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LobbyScreen(game: KtxKatan) : AbstractScreen(game) {


    private lateinit var baseView: LobbyView // Game must be init first!

    fun updateGroups(groups: List<Pair<String, String>>) {
        groups.forEach { (id, mode) ->
            baseView.addGroup("Placeholder: $mode", id)
        }
    }

    override fun buildStage() {
        logger.debug { "Building lobby" }
        baseView = LobbyView(game, Scene2DSkin.defaultSkin)
        stage.actors {
            stage.addActor(baseView)
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
