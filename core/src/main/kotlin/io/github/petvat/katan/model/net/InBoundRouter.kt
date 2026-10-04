package io.github.petvat.katan.model.net

import io.github.petvat.katan.event.*
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.model.state.toDomain
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response
import java.util.concurrent.ConcurrentHashMap


/**
 * Routers every inBound [io.github.petvat.katan.shared.protocol.OutMessage] to exactly three sinks, in order:
 * 1. Correlation: replyTo resolved the matching pending request
 * 2. Gap check: channelSeq continuity per channel -> triggers resync on gaps.
 * 3. State delta + (only where state can't express it) a one-shot event
 *
 * Runs exclusively on the pump thread.
 */
class InBoundRouter(
    private val state: ClientState,
    private val tracker: RequestTracker,
    private val events: EventSystem,
    private val onGap: (channelId: String) -> Unit
) {
    private val lastChannelSeq = ConcurrentHashMap<String, Int>()

    fun route(message: OutMessage) {
        val payload = message.payload

        message.replyTo?.let { tracker.resolve(it, payload) }

        message.channelSeq?.let { seq -> checkGap(payload, seq) }

        // 3. State + events.
        when (payload) {
            // ---- pure acks: resolve the deferred, apply nothing ----
            Response.OK -> Unit
            is Response.Error -> events.fire(ErrorEvent(reason = payload.detail, code = payload.code))

            // ---- auth ----
            is Response.Registered -> {
                state.onRegistered(payload); events.fire(LoginEvent)
            }

            is Response.Resumed -> state.onResumed(payload)
            is Response.ResumeFailed -> events.fire(LoginEvent) // or a dedicated ResumeFailedEvent

            // ---- lobby / group lifecycle ----
            is Response.GroupUpdate -> state.onGroupUpdate(payload)   // no event: VMs diff lobby
            is Response.GroupCreated -> {
                state.onGroupCreated(payload); events.fire(CreateEvent)
            }

            is Response.UserJoined -> state.onUserJoined(payload)     // no event: VMs diff members
            is Response.Joined -> {
                state.onJoined(payload); events.fire(JoinEvent)
            }

            Response.LeftOk -> {
                state.onLeft(); events.fire(LeaveEvent)
            }

            is Response.Left -> {
                state.onUserLeft(payload); events.fire(UserLeftEvent)
            }

            // ---- chat ----
            is Response.Chat -> {
                state.onChat(payload)
                events.fire(ChatEvent(payload.from, payload.message))
            }

            is Response.ChatResync -> state.onChatResync(payload)

            // ---- game: deltas ----
            is Response.Init -> state.onGameInit(
                payload.privateGameState.toDomain(
                    state.group?.id ?: throw IllegalStateException("No group."),
                )
            )

            is Response.InitSpectator -> TODO()
            is Response.GameResync -> {
                state.onGameInit(payload.privateGameState.toDomain(requireNotNull(state.game?.id)))
                events.fire(ResyncEvent)
            }

            is Response.DiceRolled -> {
                state.onDiceRolled(payload)
                events.fire(RolledDiceEvent(payload.roll1, payload.roll2, payload.moveRobber))
            }

            is Response.Build -> {
                state.onBuild(payload)
                events.fire(BuildEvent(payload.builder, payload.buildkind, payload.coordinates))
            }

            is Response.InitBuildingPlaced -> {
                state.onInitBuildPlaced(payload)
                events.fire(BuildEvent(payload.builder, payload.buildkind, payload.coordinates))
            }

            is Response.SetupEnded -> state.onSetupEnded(payload)
            is Response.EndTurn -> state.onEndTurn(payload)
            is Response.RobberMoved -> state.onRobberMoved(payload)
            is Response.InitTrade -> state.onTradeInited(payload)
            is Response.TradeDeclined -> state.onTradeDeclined(payload)
            is Response.TradeExecuted -> state.onTradeExecuted(payload)
            is Response.VictoryClaimed -> {
                state.onVictoryClaimed(payload); events.fire(VictoryEvent(payload.winner))
            }
        }
    }

    /**
     * The server sends deltas, so one missed message = silently wrong state.
     * channelSeq gives us continuity per channel. NOTE: OutMessage carries no
     * channelId — we infer it from the payload.
     * TODO: Consider adding channelId to OutMessage in the protocol later.
     */
    private fun checkGap(payload: Response, seq: Int) {
        val channelId = channelKeyOf(payload) ?: return
        val last = lastChannelSeq[channelId]
        if (last != null && seq > last + 1) onGap(channelId)
        lastChannelSeq[channelId] = maxOf(seq, last ?: seq)
    }

    // TODO: Remove, see above.
    private fun channelKeyOf(payload: Response): String? = when (payload) {
        is Response.DiceRolled -> payload.gameId
        is Response.Build -> payload.gameId
        is Response.InitBuildingPlaced -> payload.gameId
        is Response.SetupEnded -> payload.gameId
        is Response.EndTurn -> payload.gameId
        is Response.RobberMoved -> payload.gameId
        is Response.InitTrade -> payload.gameId
        is Response.TradeDeclined -> payload.gameId
        is Response.TradeExecuted -> payload.gameId
        is Response.VictoryClaimed -> payload.gameId
        is Response.GroupUpdate -> payload.groupId // lobby deltas: idempotent counts
        else -> null // snapshots and acks are self-contained
    }
}
