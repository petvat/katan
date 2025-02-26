package io.github.petvat.katan.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ktx.app.KtxApplicationAdapter
import ktx.app.KtxGame
import ktx.app.KtxScreen

fun main() = gdxTest("Simple test", KtxTest())

class MyGame : KtxApplicationAdapter {
    override fun create() {
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}

class TestScreen(game: KtxTest) : KtxScreen {

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }
}


/**
 * Main class for LibGDX view context.
 *
 */
class KtxTest() :
    KtxGame<TestScreen>() {

    override fun create() {
        addScreen(TestScreen(this))

        setScreen<TestScreen>()

    }
}
