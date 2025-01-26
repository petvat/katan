package io.github.petvat.katan.shared.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LEVEL")
enum class PermissionLevel {
    USER, GUEST
}
