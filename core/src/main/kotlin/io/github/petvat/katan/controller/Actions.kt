package io.github.petvat.katan.controller

//import io.github.petvat.katan.model.ClientState
//import io.github.petvat.katan.shared.hexlib.Coordinates
//import io.github.petvat.katan.shared.model.board.BuildKind
//import io.github.petvat.katan.shared.model.game.Settings
//import io.github.petvat.katan.shared.protocol.Request
//
//interface IGameActions {
//    fun rollDice()
//    fun build(buildKind: BuildKind, coordinates: Coordinates)
//    fun init()
//}
//
//interface IChatActions {
//    fun sendMessage(message: String)
//}
//
//interface ILobbyActions {
//    fun register(name: String)
//    fun create(settings: Settings)
//    fun join(channelId: String)
//}
//
//
//class GameActions(
//    private val sender: RequestSender,
//    private val state: ClientState
//) : IGameActions {
//    override fun rollDice() {
//        sender.send(state.game.id, Request.RollDice)
//    }
//
//    override fun build(buildKind: BuildKind, coordinates: Coordinates) {
//        sender.send(state.game.id, Request.Build(buildKind, coordinates))
//    }
//
//    override fun init() {
//        sender.send(state.game.id, Request.Init)
//    }
//}
//
//// TODO: Chat disabled.
//class ChatActions(
//    private val sender: RequestSender,
//    private val state: ClientState
//) : IChatActions {
//    override fun sendMessage(message: String) {
//        sender.send(state.chat!!.id, Request.Chat(message))
//    }
//}
//
//class LobbyActions(private val sender: RequestSender, private val state: ClientState) : ILobbyActions {
//    override fun register(name: String) {
//        sender.send(channel = null, Request.GuestRegister(name))
//    }
//
//    override fun create(settings: Settings) {
//        sender.send(channel = state.lobby, Request.Create(settings)) ?: -1
//    }
//
//    override fun join(channelId: String) {
//        sender.send(channel = null, Request.Join(channelId))
//    }
//}
