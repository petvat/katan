package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.viewmodel.LoginViewModel
import ktx.actors.onChangeEvent
import ktx.actors.onKeyUp
import ktx.scene2d.*


class LoginView(
    val viewModel: LoginViewModel,
    skin: Skin
) : KtxView(skin), KTable {

    // private val  settingsWidget

    private val nameInput: TextField

    private val registerBtn: TextButton

    init {
        setFillParent(true)
        align(Align.center)

        nameInput = scene2d.textField {
            onKeyUp {
                if (it == Input.Keys.ENTER && text.isNotBlank()) {
                    println(text)
                    this@LoginView.viewModel.registerAsGuest(text)
                    text = "" // reset
                }
            }
        }
        registerBtn = scene2d.textButton("Register as Guest") {


            onChangeEvent {
                this@LoginView.viewModel.registerAsGuest(
                    this@LoginView.nameInput.text // With input text
                )
            }
        }
        add(nameInput)
        row().space(5f)
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
