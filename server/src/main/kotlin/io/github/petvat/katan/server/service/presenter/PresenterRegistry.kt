package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.presenter.game.RollDicePresenter
import io.github.petvat.katan.server.service.presenter.group.JoinGroupPresenter
import io.github.petvat.katan.server.service.event.LobbyEvent
import io.github.petvat.katan.server.service.presenter.lobby.RegisterPresenter
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


interface Registry<K : Any, V : Any> {
    fun <T : K> register(key: KClass<T>, value: V)
    fun <T : K> register(key: T, value: V)
    fun <T : K> unregister(key: KClass<T>)
    fun <T : K> unregister(key: T)
    fun <T : K> get(key: KClass<T>): V
    fun <T : K> get(key: T): V
}


open class ConcurrentRegistry<K : Any, V : Any> : Registry<K, V> {
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

    override fun <T : K> get(key: T): V {
        return entries[key::class] ?: error("No value registered for key: $key")
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


///**
// * This class is registry for all presenters.
// */
class PresenterRegistry(
) {
    init {
        register(LobbyEvent.Registered::class, RegisterPresenter())
        register(GameEvent.DiceRolledSummary::class, RollDicePresenter())
        register(GroupEvent.Joined::class, JoinGroupPresenter())
        register(GroupEvent.Init::class, Init)
    }

    private val presenters = mutableMapOf<KClass<out Event>, Presenter<out Event, out Channel<*>>>()

    private fun <E : Event> register(eventClass: KClass<E>, presenter: Presenter<E, out Channel<*>>) {
        presenters[eventClass] = presenter
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> getPresenterFor(event: E): Presenter<E, Channel<*>> {
        return presenters[event::class] as? Presenter<E, Channel<*>>
            ?: error("No presenter registered for event type: ${event::class}")

    }
}
