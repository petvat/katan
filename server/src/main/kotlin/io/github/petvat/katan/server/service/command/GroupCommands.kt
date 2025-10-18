package io.github.petvat.katan.server.service.command

import io.github.petvat.katan.server.service.service.ChannelId


data class InitGame(override val groupId: ChannelId) : GroupCommand

data class JoinGroup(override val groupId: ChannelId) : GroupCommand



