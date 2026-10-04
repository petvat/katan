package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.viewmodel.StartMenuViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*


class StartMenuView(
    val viewModel: StartMenuViewModel,
    skin: Skin
) : KtxView(skin), KTable {

    // private val  settingsWidget

    init {
        setFillParent(true)
        align(Align.center)

        val innertbl = scene2d.table {
            background = skin.getDrawable("area")

//        textField {
//            onKeyUp {
//                if (it == Input.Keys.ENTER) {
//                    println(text)
//                    onChangeEvent { this@StartMenuView.viewModel.connectToclient() }
//                    text = "" // reset
//                }
//            }
//        }
//        row()

            textButton("Connect") {
                onChangeEvent { this@StartMenuView.viewModel.connect() } // inlined
            }
            row().space(10f)
            textButton("Settings") {
                onChangeEvent {
                    println("click.")
                }
            }

        }

        add(innertbl).maxHeight(700f).maxWidth(700f)


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
//    ktxCtx: KtxKatan,
//    skin: Skin
//) : Table(skin), KTable, EventListener {
//
//    init {
//        setFillParent(true)
//        //background("area")
//        align(Align.center)
//        debug = true
//        textButton("Connect to host").onChange {
//            if (ktxCtx.controller.connectClient(null, null)) {
//                ktxCtx.showLobbyView()
//            } else {
//                ktxCtx.showErrorView("Could not connect to server!")
//            }
//            // ktxCtx.showLobbyView()
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
//    ktxCtx: KtxKatan,
//    skin: Skin,
//    init: MenuView.(S) -> Unit = {}
//): MenuView = actor(MenuView(ktxCtx, skin), init)
