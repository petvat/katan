package io.github.petvat.katan.model

import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.petvat.katan.shared.protocol.dto.RestrictedGroupView
import io.github.petvat.katan.view.KatanView


interface ViewState {
    val model: KatanModel
    fun show(view: KatanView)
    fun <R : Response> handle(delta: R)
}


open class LoginState(override val model: KatanModel) : ViewState {
    override fun show(view: KatanView) {
        // view.showLogin()
    }

    /**
     * NOTE: not reliable.
     */
    override fun <R : Response> handle(delta: R) {
        model.userInfo = (delta as Response.Login).userInfo
        model.accessToken = delta.token
        handleTransition()
    }

    fun handleTransition() {
        model.clientState = LobbyState(model)
    }
}

class LobbyState(override val model: KatanModel) : ViewState {
    override fun show(view: KatanView) {
        view.showLobbyView()
    }

    override fun <R : Response> handle(delta: R) {
        when (delta) {
            is Response.Join -> {
                val group = delta.groupView
                // handleTransition(group)
            }

            is Response.Create -> {

            }
        }

    }

    fun handleTransition() {
        TODO("Not yet implemented")
    }

    private fun handleTransition(group: RestrictedGroupView) {
        //model.clientState = GroupState(model, group)
    }

}

class GroupState(override val model: KatanModel) : ViewState {
    override fun show(view: KatanView) {
        // view.showSession
    }

    override fun <R : Response> handle(delta: R) {
        TODO("Not yet implemented")
    }

//    override fun handleTransition() {
//        TODO("Not yet implemented")
//    }

}


