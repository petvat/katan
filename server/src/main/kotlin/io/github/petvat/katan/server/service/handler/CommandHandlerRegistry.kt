package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.command.*
import io.github.petvat.katan.server.service.presenter.ConcurrentRegistry
import kotlin.reflect.KClass

class CommandHandlerRegistry {

    init {
        register(Register::class, RegisterHandler())
        register(RollDice::class, RollDiceHandler())
        register(JoinGroup::class, JoinGroupHandler())
    }

    private val handlers = mutableMapOf<KClass<out Command>, CommandHandler<out Command, out Channel<*>>>()

    private fun <C : Command> register(eventClass: KClass<C>, presenter: CommandHandler<C, out Channel<*>>) {
        handlers[eventClass] = presenter
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Command> getHandlerFor(event: C): CommandHandler<C, Channel<*>> {
        return handlers[event::class] as? CommandHandler<C, Channel<*>>
            ?: error("No presenter registered for event type: ${event::class}")

    }
}

