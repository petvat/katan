package io.github.petvat.katan.model
//
//import io.github.petvat.katan.shared.protocol.dto.Response
//import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView
//import io.github.petvat.katan.ui.KatanUI
//
//// TODO: REMOVE
//
//interface ViewState {
//    val model: KatanModel
//    fun show(view: KatanUI)
//    fun <R : Response> handle(delta: R)
//}
//
//
//open class LoginState(override val model: KatanModel) : ViewState {
//    override fun show(view: KatanUI) {
//        // view.showLogin()
//    }
//
//    /**
//     * NOTE: not reliable.
//     */
//    override fun <R : Response> handle(delta: R) {
//        model.userInfo = (delta as Response.Login).userInfo
//        model.accessToken = delta.token
//        handleTransition()
//    }
//
//    fun handleTransition() {
//        model.clientState = LobbyState(model)
//    }
//}
//
//class LobbyState(override val model: KatanModel) : ViewState {
//    override fun show(view: KatanUI) {
//        view.showLobbyView()
//    }
//
//    override fun <R : Response> handle(delta: R) {
//        when (delta) {
//            is Response.Join -> {
//                val group = delta.groupView
//                // handleTransition(group)
//            }
//
//            is Response.Create -> {
//
//            }
//        }
//
//    }
//
//    fun handleTransition() {
//        TODO("Not yet implemented")
//    }
//
//    private fun handleTransition(group: PrivateGroupView) {
//        //model.clientState = GroupState(model, group)
//    }
//
//}
//
//class GroupState(override val model: KatanModel) : ViewState {
//    override fun show(view: KatanUI) {
//        // view.showSession
//    }
//
//    override fun <R : Response> handle(delta: R) {
//        TODO("Not yet implemented")
//    }
//
////    override fun handleTransition() {
////        TODO("Not yet implemented")
////    }
//
//}


