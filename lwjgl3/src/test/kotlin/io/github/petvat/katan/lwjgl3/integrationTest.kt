package io.github.petvat.katan.lwjgl3


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.ErrorEvent
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventListener
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.widget.createErrorWindow
import io.github.petvat.katan.ui.model.ViewModel
import ktx.actors.stage
import ktx.app.KtxApplicationAdapter
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.table

//fun main() = gdxTest("Simple test", MyGame())
//
//class MyGame : KtxApplicationAdapter {
//    override fun create() {
//
//    }
//
//    override fun resize(width: Int, height: Int) {
//
//    }
//
//    override fun render() {
//        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//    }
//
//    override fun pause() {
//    }
//
//    override fun resume() {
//    }
//
//    override fun dispose() {
//    }
//}
//
//class TestScreen(game: KtxTest) : KtxScreen {
//    override fun render(delta: Float) {
//        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//    }
//}
//
//
///**
// * Main class for LibGDX view context.
// *
// */
//class KtxTest() :
//    KtxGame<TestScreen>() {
//
//    override fun create() {
//        addScreen(TestScreen(this))
//
//        setScreen<TestScreen>()
//
//    }
//}
//
