package io.github.petvat.katan.model.command

import io.github.petvat.katan.model.net.RequestTracker
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import kotlinx.coroutines.CompletableDeferred

typealias Future = CompletableDeferred<Response>?

/**
 * User intent. One method per thing the user can ask for.
 * Channel ids are resolved from ClientState; transport details are hidden.
 */
interface GameCommands {
    fun rollDice(): Future
    fun build(kind: BuildKind, at: Coordinates): Future
    fun moveRobber(to: HexCoord): Future
    fun steal(fromPlayer: Int): Future
    fun initTrade(offer: ResourceMap, inReturn: ResourceMap, targets: Set<Int>): Future
    fun respondTrade(tradeId: Int, accept: Boolean): Future
    fun endTurn(): Future
    fun claimVictory(): Future
    fun requestResync(): Future
    fun chat(message: String): Future
}

interface GroupCommands {
    fun chat(message: String): Future
    fun leave(): Future
    fun init(): Future

}

// TODO: Group commands
interface LobbyCommands {
    fun register(name: String): Future
    fun resume(token: String): Future
    fun join(channelId: String): Future
    fun create(settings: Settings): Future
}

interface ChatCommands {
    fun send(message: String): Future
}

class KatanCommands(
    val game: GameCommands,
    val lobby: LobbyCommands,
    val chat: ChatCommands,
    val group: GroupCommands,
)

class GameCommandsImpl(
    private val tracker: RequestTracker,
    private val state: ClientState
) : GameCommands {
    private fun send(request: Request) = tracker.send(state.game?.id, request)
    override fun rollDice() = send(Request.RollDice)
    override fun build(kind: BuildKind, at: Coordinates) = send(Request.Build(kind, at))
    override fun moveRobber(to: HexCoord) = send(Request.MoveRobber(to))
    override fun steal(fromPlayer: Int) = send(Request.Steal(fromPlayer))
    override fun initTrade(offer: ResourceMap, inReturn: ResourceMap, targets: Set<Int>) =
        send(Request.InitTrade(targets, offer, inReturn))

    override fun respondTrade(tradeId: Int, accept: Boolean) = send(Request.RespondTrade(tradeId, accept))
    override fun endTurn() = send(Request.EndTurn)
    override fun claimVictory() = send(Request.ClaimVictory)
    override fun requestResync() = send(Request.Init)
    override fun chat(message: String) = send(Request.Chat(message))
}

class GroupCommandsImpl(
    private val tracker: RequestTracker,
    private val state: ClientState
) : GroupCommands {
    private fun send(request: Request) = tracker.send(state.group?.id, request)
    override fun chat(message: String) = tracker.send(state.chat?.id, Request.Chat(message))
    override fun leave() = send(Request.Leave)
    override fun init() = send(Request.Init)

}

class LobbyCommandsImpl(
    private val tracker: RequestTracker,
    private val state: ClientState
) : LobbyCommands {
    override fun register(name: String) = tracker.send(null, request = Request.GuestRegister(name))
    override fun resume(token: String) =
        tracker.send(channelId = state.auth.lobbyChannel, request = Request.Resume(token))

    override fun join(channelId: String) =
        tracker.send(channelId = channelId, request = Request.Join(channelId))

    override fun create(settings: Settings) = tracker.send(state.auth.lobbyChannel, Request.Create(settings))
}

class ChatCommandsImpl(
    private val tracker: RequestTracker,
    private val state: ClientState
) : ChatCommands {
    override fun send(message: String) =
        tracker.send(requireNotNull(state.chat?.id) { "Chat channel ID is null." }, Request.Chat(message))
}
