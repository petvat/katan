package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupFactory
import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.command.*
import kotlin.reflect.KClass

class CommandHandlerRegistry(
    channelRegistry: ChannelRegistry,
) {

    private val handlers = mutableMapOf<KClass<out Command>, CommandHandler<out Command, out Channel<*>>>()

    init {
        // GROUP
        register(JoinGroup::class, JoinGroupHandler())
        register(LeaveGroup::class, LeaveGroupHandler())
        register(CreateGroup::class, CreateGroupHandler(channelRegistry))
        register(InitGame::class, InitGameHandler(channelRegistry))

        // GAME
        register(RollDice::class, RollDiceHandler())
        register(Build::class, BuildHandler())
        register(EndTurn::class, EndTurnHandler())
        register(BuildInitSettlment::class, BuildInitialSettlementHandler())

        // CHAT
        register(Chat::class, ChatHandler())
    }


    private fun <C : Command> register(eventClass: KClass<C>, presenter: CommandHandler<C, out Channel<*>>) {
        handlers[eventClass] = presenter
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Command> getHandlerFor(event: C): CommandHandler<C, Channel<*>> {
        return handlers[event::class] as? CommandHandler<C, Channel<*>>
            ?: error("No presenter registered for event type: ${event::class}")

    }
}

