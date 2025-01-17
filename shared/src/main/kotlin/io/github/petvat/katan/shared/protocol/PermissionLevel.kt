package io.github.petvat.katan.shared.protocol

import kotlinx.serialization.Serializable

@Serializable
enum class PermissionLevel {
    USER, GUEST
}
