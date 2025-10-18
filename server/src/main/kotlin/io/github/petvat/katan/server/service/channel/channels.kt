package io.github.petvat.katan.server.service.channel

import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.concurrency.Lockable
import io.github.petvat.katan.server.service.engine.GameSnapshot
import io.github.petvat.katan.server.service.presenter.ConcurrentRegistry
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.game.Settings
import java.util.concurrent.ConcurrentHashMap

@JvmInline
value class ChannelId(
    val value: String
)


// Channel has Session. Every type of channel has it.

interface Channel<S : Subscriber> : Lockable {
    var seq: Int
    val id: ChannelId
    val subs: ConcurrentHashMap<UserId, S>

    fun subscribe(id: UserId, subscriber: S) {
        subs[id] = subscriber
    }

    fun unsubscribe(id: UserId) {
        subs.remove(id)
    }
}

class ChannelRegistry : ConcurrentRegistry<ChannelId, Channel<*>>() {
//    private val channels = ConcurrentHashMap<ChannelId, Channel<*>>()
//
//    // ???
//    private val subscribers = ConcurrentHashMap<ChannelId, List<Pair<ClientId, Subscriber>>>()
//
//    operator fun get(id: ChannelId) = channels[id]
//    operator fun plusAssign(channel: Channel<*>) {
//        channels[channel.id] = channel
//    }
//
//    operator fun minusAssign(id: ChannelId) {
//        channels.remove(id)
//    }
}


/**
 * TODO: This has to be user id.
 */
class GameChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, GameSubscriber>,
    var snapshot: GameSnapshot,
    val clientToPlayerId: Map<UserId, Int>, override var seq: Int = 0
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
    val settings: Settings,
    val lobby: LobbyChannel,
    override var seq: Int = 0
) : Channel<GroupSubscriber> {
    init {
        require(id.value.startsWith("group:")) {
            "GroupChannel.id must start with 'group:', but was '${id.value}'"
        }
    }
}

class ChatChannel(
    override val id: ChannelId,
    override val subs: ConcurrentHashMap<UserId, ChatSubscriber>,
    val chatHistory: List<ChatMessage>,
    override var seq: Int = 0
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
    override var seq: Int = 0
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
