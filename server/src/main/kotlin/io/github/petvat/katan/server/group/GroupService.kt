package io.github.petvat.katan.server.group

import io.github.petvat.katan.server.client.*
import io.github.petvat.katan.shared.model.game.Settings
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object GroupService {

    private val _groups = ConcurrentHashMap<GroupId, Group>()

    val groups get(): Map<GroupId, Group> = _groups.toMap() // TODO: FIX

    private fun generateGroupId(): String {
        return UUID.randomUUID().toString()
    }

    fun addGroup(group: Group): Group {
        _groups[group.id] = group
        return group
    }

    fun addGroup(clientState: ClientState, settings: Settings): Group {
        when (clientState) {
            is LoggedInState -> {
                return addGroup(
                    UserGroup(
                        GroupId(generateGroupId()),
                        mutableMapOf(
                            clientState.sessionId to GroupMember(
                                clientState.sessionId,
                                UserService.getUser(clientState.userId).username
                            )
                        ),
                        clientState.sessionId,
                        level = clientState.level,
                        settings = settings

                    )
                )
            }
            // TODO: Refactor
            is GuestState -> {
                return addGroup(
                    UserGroup(
                        GroupId(generateGroupId()),
                        mutableMapOf(clientState.sessionId to GroupMember(clientState.sessionId, "Guest")),
                        clientState.sessionId,
                        level = clientState.level,
                        settings = settings

                    )
                )
            }

            is InGroupState -> throw IllegalArgumentException("Already in a group!")
            is PlayingState -> throw IllegalArgumentException("Already in a group!")
        }
    }

    private fun updateGroup(group: Group) {
        _groups[group.id] = group
    }

    fun elevateToGame(groupId: GroupId): Game? {
        return when (val base = _groups[groupId]) {
            is UserGroup, is GuestGroup -> {
                val game = Game(base)
                updateGroup(game)
                game
            }

            else -> null
        }
    }

    fun degradeToBase(gameId: GroupId): Group? {
        return when (val game = _groups[gameId]) {
            is Game -> {
                updateGroup(game.base)
                game
            }

            else -> null
        }
    }


    fun getGameFromGroupId(groupId: GroupId): Game? {
        val group = groups[groupId]
        return group as? Game
    }


}
