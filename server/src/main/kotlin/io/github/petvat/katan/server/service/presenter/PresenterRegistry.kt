package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.*
import io.github.petvat.katan.server.service.presenter.chat.ChatPresenter
import io.github.petvat.katan.server.service.presenter.game.RollDicePresenter
import io.github.petvat.katan.server.service.presenter.group.JoinGroupPresenter
import io.github.petvat.katan.server.service.presenter.game.BuildInitialSettlementPresenter
import io.github.petvat.katan.server.service.presenter.game.BuildPresenter
import io.github.petvat.katan.server.service.presenter.game.InitGamePresenter
import io.github.petvat.katan.server.service.presenter.group.CreateGroupPresenter
import io.github.petvat.katan.server.service.presenter.group.LeaveGroupPresenter
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

interface KeyedRegistry<K : Any, V : Any> {
    fun register(key: K, value: V)
    fun unregister(key: K)
    fun get(key: K): V?
    fun all(): List<V>
}

open class ConcurrentKeyedRegistry<K : Any, V : Any> : KeyedRegistry<K, V> {
    private val entries = ConcurrentHashMap<K, V>()
    override fun register(key: K, value: V) {
        entries[key] = value
    }

    override fun unregister(key: K) {
        entries.remove(key)
    }

    override fun get(key: K): V? = entries[key]
    override fun all(): List<V> = entries.values.toList()
}

interface TypedRegistry<K : Any, V : Any> {
    fun <T : K> register(key: KClass<T>, value: V)
    fun <T : K> register(key: T, value: V)
    fun <T : K> unregister(key: KClass<T>)
    fun <T : K> unregister(key: T)
    fun <T : K> get(key: KClass<T>): V?
    fun <T : K> get(key: T): V?
}


open class ConcurrentTypedRegistry<K : Any, V : Any> : TypedRegistry<K, V> {
    private val entries = ConcurrentHashMap<KClass<out K>, V>()

    override fun <T : K> register(key: KClass<T>, value: V) {
        entries[key] = value
    }

    override fun <T : K> register(key: T, value: V) {
        entries[key::class] = value
    }

    override fun <T : K> unregister(key: KClass<T>) {
        entries.remove(key)
    }

    override fun <T : K> unregister(key: T) {
        entries.remove(key::class)
    }

    override fun <T : K> get(key: KClass<T>): V {
        return entries[key] ?: error("No value registered for key: $key")
    }

    override fun <T : K> get(key: T): V? {
        return entries[key::class]
    }
}


//class PresenterRegistry(userRegistry: UserRegistry) :
//    ConcurrentRegistry<Event, Presenter<out Event, out Channel<*>>>() {
//    init {
//        register(LobbyEvent.Registered::class, RegisterPresenter())
//        register(GameEvent.DiceRolledSummary::class, RollDicePresenter())
//        register(GroupEvent.Joined::class, JoinGroupPresenter(userRegistry))
//        register(GroupEvent.Init::class)
//    }
//}


/**
 * This class is registry for all presenters.
 */
class PresenterRegistry(
    userRegistry: UserRegistry,
    channelRegistry: ChannelRegistry
) {
    private val presenters = mutableMapOf<KClass<out Event>, Presenter<out Event>>()


    init {
        // LOBBY

        // TODO: Add this
        //register(LobbyEvent.Registered::class, RegisterPresenter())

        // GROUP
        register(GroupEvent.Joined::class, JoinGroupPresenter(userRegistry, channelRegistry))
        register(GroupEvent.Created::class, CreateGroupPresenter(channelRegistry))
        register(GroupEvent.Left::class, LeaveGroupPresenter(channelRegistry))

        // GAME
        register(GameEvent.DiceRolledSummary::class, RollDicePresenter(channelRegistry))
        register(GameEvent.Init::class, InitGamePresenter(channelRegistry))
        register(GameEvent.BuiltInitial::class, BuildInitialSettlementPresenter(channelRegistry))
        register(GameEvent.Built::class, BuildPresenter(channelRegistry))

        // CHAT
        register(ChatEvent::class, ChatPresenter(channelRegistry))
    }


    private fun <E : Event> register(eventClass: KClass<E>, presenter: Presenter<E>) {
        presenters[eventClass] = presenter
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> getPresenterFor(event: E): Presenter<E> {
        return presenters[event::class] as? Presenter<E>
            ?: error("No presenter registered for event type: ${event::class}")

    }
}
