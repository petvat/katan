package io.github.petvat.katan.shared.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("lvl")
enum class PermissionLevel {
    USER, GUEST, UNAUTH
}
