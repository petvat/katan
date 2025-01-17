package io.github.petvat.katan.view

import io.github.petvat.katan.model.KatanModel

interface KatanView {

    fun showNewChatMessage()
    fun showLoggedInView()
    fun showGroupView()
    fun showGameView()
    fun showLobbyView()
    fun showErrorView(error: String)
    fun showGameUpdate()
    fun updateGroupsView(groups: List<Pair<String, String>>)
}
