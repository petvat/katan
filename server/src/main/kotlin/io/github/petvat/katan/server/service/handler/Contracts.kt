package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.command.*
import io.github.petvat.katan.server.service.engine.GameRuleEngine
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface CommandHandler<C : Command, R : Channel<*>> {
    /**
     * Exectues command by [execute] but ticks the channel sequence number if the execution succeeded.
     */
    fun handle(client: ConnectedClient, channel: R, command: C): Event {
        val event = execute(client, channel, command)


        // TODO: Make sure this is not done twice another place.
        if (event !is Event.Failure) {
            val tickedSeq = channel.nextSeq()
            event.channelSeq = tickedSeq
        }
        return event
    }

    fun execute(client: ConnectedClient, channel: R, command: C): Event
}

interface GameCommandHandler<C : GameCommand> : CommandHandler<C, GameChannel> {
    override fun execute(client: ConnectedClient, channel: GameChannel, command: C): Event {
        val game = channel.snapshot

        if (channel.subs[client.auth.id] != GameSubscriber.Player) {
            return Event.Failure("User is not part of the game.", ErrorCode.DENIED)
        }

        val player = channel.userToPlayerId[client.auth.id]!!

        // TODO: Very redundant! Find way to inject without creating
        val gameRuleEngine = GameRuleEngine(game.rules)

        val event = executeGameAction(channel, player, gameRuleEngine, command)
        return event
    }

    fun executeGameAction(channel: GameChannel, player: Int, engine: GameRuleEngine, command: C): GameEvent
}

interface GroupCommandHandler<C : GroupCommand> : CommandHandler<C, GroupChannel> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: C): Event
}

interface LobbyCommandHandler<C : LobbyCommand> : CommandHandler<C, LobbyChannel> {
    override fun execute(client: ConnectedClient, channel: LobbyChannel, command: C): Event
}

interface ChatCommandHandler : CommandHandler<Chat, ChatChannel> {
    override fun execute(client: ConnectedClient, channel: ChatChannel, command: Chat): Event
}

