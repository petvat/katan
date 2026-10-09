package io.github.petvat.katan.server.service.channel

interface Subscriber

sealed interface GameSubscriber : Subscriber {
    data object Spectator : GameSubscriber
    data object Player : GameSubscriber
}

sealed interface GroupSubscriber : Subscriber {
    data object Member : GroupSubscriber
    data object Host : GroupSubscriber
}

sealed interface ChatSubscriber : Subscriber {
    data object Member : ChatSubscriber
}

sealed interface LobbySubscriber : Subscriber {
    data object Member : LobbySubscriber
}
