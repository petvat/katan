package io.github.petvat.katan.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.InEventBus
import io.github.petvat.katan.shared.model.session.PrivateGameState
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.model.*

//
//class ViewModelManager(val controller: RequestController, val transitionService: ViewTransitionService) {
//
//    var gameViewModel: GameViewModel? = null
//    var startMenuViewModel: StartMenuViewModel? = StartMenuViewModel(controller, transitionService)
//    var lobbyViewModel: LobbyViewModel? = null
//    var groupViewModel: GroupViewModel? = null
//
//
//    var currentViewModel: ViewModel? = null
//
//
//    fun onChangeToGameView(dto: PrivateGameState, group: PrivateGroupView) {
//        gameViewModel = GameViewModel(controller, group, dto)
//        InEventBus.registerViewModel(gameViewModel!!)
//    }
//
//    fun onChangeToGroupView(dto: PrivateGroupView) {
//        groupViewModel = GroupViewModel(dto, controller, transitionService)
//        InEventBus.registerViewModel(groupViewModel!!)
//    }
//
//    // TODO: Mutex wrap.
//    // TODO: Wrap transitionService and controller for more concise code.
//    fun initGameViewModel(dto: PrivateGameState) {
//        if (gameViewModel != null) {
//            InEventBus.unregisterViewModel(gameViewModel)
//        }
//
//        InEventBus.registerViewModel(gameViewModel!!)
//    }
//
//    fun initLobbyView() {
//        lobbyViewModel = LobbyViewModel(controller, transitionService, groups)
//        InEventBus.registerViewModel(lobbyViewModel!!)
//    }
//
//    fun initMenuViewModel() {
//        startMenuViewModel = StartMenuViewModel(controller, transitionService)
//        InEventBus.registerViewModel(startMenuViewModel!!)
//    }
//
//    fun initGroupViewModel(group: PrivateGroupView) {
//        groupViewModel = GroupViewModel(group, controller, transitionService)
//        InEventBus.registerViewModel(groupViewModel!!)
//    }
//
//    fun initUserProfile() {
//
//    }
//
//}
