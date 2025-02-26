package io.github.petvat.katan.group

import io.github.petvat.katan.server.client.*
import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
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

    fun getGroupsPublic(): List<PublicGroupDTO> {
        return groups.values.map { toPublic(it) }
    }

    fun toPrivate(group: Group, excluding: SessionId): PrivateGroupDTO {
        return PrivateGroupDTO(
            id = group.id.value,
            clients = group.clients
                .filter { (s, _) -> s != excluding }
                .map { (sid, mem) -> sid.value to mem.name }
                .toMap()
                .toMutableMap(),
            level = group.level,
            chatLog = group.chatLog,
            settings = group.settings
        )
    }

    fun toPublic(group: Group): PublicGroupDTO {
        return PublicGroupDTO(
            id = group.id.value,
            numClients = group.clients.size,
            maxClients = group.settings.maxPlayers,
            level = group.level,
            mode = group.settings.gameMode
        )
    }

    suspend fun removeFromGroup(sessionId: SessionId, groupId: GroupId) {
        _groups[groupId]?.remove(sessionId) // Some assertion here maybe!
    }

    suspend fun addToGroup(clientState: ConnectedClient, group: Group) {
        if (clientState.activity !is Idle) {
            throw IllegalArgumentException("This client is already in a group")
        }

        if (clientState.auth.level != group.level) {
            throw IllegalArgumentException("Permission levels mismatch.")
        }

        if (group.isFull()) {
            throw IllegalArgumentException("Group is full.")
        }

        val groupMember = GroupMember(
            clientState.sessionId,
            when (clientState.auth) {
                is LoggedInAuth -> UserService.getUser(clientState.auth.userId).username
                is GuestAuth -> clientState.auth.tempName
                else -> throw IllegalStateException("Unauth.")
            }
        )

        group.add(groupMember)
    }

    fun addGroup(clientState: ConnectedClient, settings: Settings): Group {
        if (clientState.activity !is Idle) {
            throw IllegalArgumentException("This client is already in group.")
        }

        val name: String = when (clientState.auth) {
            is GuestAuth -> clientState.auth.tempName
            is LoggedInAuth -> UserService.getUser(clientState.auth.userId).username
            else -> throw IllegalStateException("Unauth.")
        }

        return addGroup(
            UserGroup(
                id = GroupId(generateGroupId()),
                clients = mutableMapOf(
                    clientState.sessionId to GroupMember(
                        clientState.sessionId,
                        name
                    )
                ),
                host = clientState.sessionId,
                level = clientState.auth.level,
                settings = settings
            )
        )
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
