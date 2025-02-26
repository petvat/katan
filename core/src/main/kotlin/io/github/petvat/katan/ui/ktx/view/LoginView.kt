package io.github.petvat.core.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.LoginViewModel
import io.github.petvat.katan.ui.model.StartMenuViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*


class LoginView(
    viewModel: LoginViewModel,
    skin: Skin
) : View<LoginViewModel>(skin, viewModel), KTable {

    // private val  settingsWidget

    private val textField: TextField

    init {
        setFillParent(true)
        align(Align.center)

        textField = textField { }

        textButton("Register as Guest") {
            onChangeEvent {
                this@LoginView.viewModel.registerAsGuest(
                    this@LoginView.textField.text // With input text
                )
            } // inlined
        }
    }

    override fun registerOnPropertyChanges() {}

}

@Scene2dDsl
fun <S> KWidget<S>.loginView(
    loginViewModel: LoginViewModel,
    skin: Skin,
    init: LoginView.(S) -> Unit = {}
): LoginView = actor(LoginView(loginViewModel, skin), init)
