package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.StartMenuViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*


class StartMenuView(
    viewModel: StartMenuViewModel,
    skin: Skin
) : View<StartMenuViewModel>(skin, viewModel), KTable {

    // private val  settingsWidget

    init {
        setFillParent(true)
        align(Align.center)

        textButton("Connect to host") {
            onChangeEvent { this@StartMenuView.viewModel.connectToclient() } // inlined
        }
        row()
        textButton("Settings") {
            onChangeEvent { println("click.") }
        }
    }

    override fun registerOnPropertyChanges() {}

}

@Scene2dDsl
fun <S> KWidget<S>.startView(
    menuViewModel: StartMenuViewModel,
    skin: Skin,
    init: StartMenuView.(S) -> Unit = {}
): StartMenuView = actor(StartMenuView(menuViewModel, skin), init)


//class MenuView(
//    game: KtxKatan,
//    skin: Skin
//) : Table(skin), KTable, EventListener {
//
//    init {
//        setFillParent(true)
//        //background("area")
//        align(Align.center)
//        debug = true
//        textButton("Connect to host").onChange {
//            if (game.controller.connectClient(null, null)) {
//                game.showLobbyView()
//            } else {
//                game.showErrorView("Could not connect to server!")
//            }
//            // game.showLobbyView()
//        }
//        row()
//        textButton("Settings").onChange { println("TODO: implement settings") }
//        row()
//        textButton("Placeholder").onChange { println("TODO: implement ?") }
//    }
//
//    override fun onEvent(event: Event) {
//        if (event is LobbyEvent) {
//
//        }
//    }
//}
//
//@Scene2dDsl
//fun <S> KWidget<S>.menuView(
//    game: KtxKatan,
//    skin: Skin,
//    init: MenuView.(S) -> Unit = {}
//): MenuView = actor(MenuView(game, skin), init)
