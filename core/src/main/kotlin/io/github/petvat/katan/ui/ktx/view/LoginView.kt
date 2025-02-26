package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.LoginViewModel
import ktx.actors.onChangeEvent
import ktx.scene2d.*


class LoginView(
    viewModel: LoginViewModel,
    skin: Skin
) : View<LoginViewModel>(skin, viewModel), KTable {

    // private val  settingsWidget

    private val nameInput: TextField

    private val registerBtn: TextButton

    init {
        setFillParent(true)
        align(Align.center)

        nameInput = scene2d.textField { }
        registerBtn = textButton("Register as Guest") {
            onChangeEvent {
                this@LoginView.viewModel.registerAsGuest(
                    this@LoginView.nameInput.text // With input text
                )
            }
        }
        add(nameInput)
        row().grow()
        add(registerBtn)
    }

    override fun registerOnPropertyChanges() {}
}

@Scene2dDsl
fun <S> KWidget<S>.loginView(
    loginViewModel: LoginViewModel,
    skin: Skin,
    init: LoginView.(S) -> Unit = {}
): LoginView = actor(LoginView(loginViewModel, skin), init)
