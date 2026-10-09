package io.github.petvat.katan.server.service.channel

import io.github.petvat.katan.server.service.concurrency.Lockable
import io.github.petvat.katan.server.service.engine.GameState
import io.github.petvat.katan.server.service.presenter.ConcurrentKeyedRegistry
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.game.GameMeta
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.dto.UserData
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2

@JvmInline
value class ChannelId(
    val value: String
)


// Channel has Session. Every type of channel has it.

interface Channel<S : Subscriber> : Lockable {
    var seqCounter: Int
    val id: ChannelId
    val subs: ConcurrentHashMap<UserId, S>

    fun nextSeq(): Int = ++seqCounter

    fun subscribe(id: UserId, subscriber: S) {
        subs[id] = subscriber
    }

    fun unsubscribe(id: UserId) {
        subs.remove(id)
    }
}

class ChannelRegistry : ConcurrentKeyedRegistry<ChannelId, Channel<*>>()


class GameChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, GameSubscriber>,
    var snapshot: GameState,
    val userToPlayerId: Map<UserId, Int>, override var seqCounter: Int = 0,
    val chat: ChatChannel?
) : Channel<GameSubscriber> {

    init {
        require(id.value.startsWith("game:")) {
            "GameChannel.id must start with 'game:', but was '${id.value}'"
        }
    }
}

/**
 * TODO: This has to be user id. Must be done.
 */
class GroupChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, GroupSubscriber>,
    val host: UserId,
    val settings: Settings,
    val lobby: LobbyChannel,
    val chat: ChatChannel? = null,
    override var seqCounter: Int = 0
) : Channel<GroupSubscriber> {

    var chatEnabled = chat != null

    init {
        require(id.value.startsWith("group:")) {
            "GroupChannel.id must start with 'group:', but was '${id.value}'"
        }
    }
}

class ChatChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, ChatSubscriber>,
    val chatHistory: MutableList<ChatMessage>,
    override var seqCounter: Int = 0
) : Channel<ChatSubscriber> {
    init {
        require(id.value.startsWith("chat:")) {
            "ChatChannel.id must start with 'chat:', but was '${id.value}'"
        }
    }
}

class LobbyChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, LobbySubscriber>,
    override var seqCounter: Int = 0
) : Channel<LobbySubscriber> {
    init {
        require(id.value.startsWith("lobby:")) {
            "LobbyChannel.id must start with 'lobby:', but was '${id.value}'"
        }
    }
}

data class ChatMessage(
    val userId: UserId,
    val message: String
)
