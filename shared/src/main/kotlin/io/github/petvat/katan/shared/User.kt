package io.github.petvat.katan.shared

import io.github.petvat.katan.shared.model.dto.PrivateUserDTO
import io.github.petvat.katan.shared.model.dto.PublicUserDTO

@JvmInline
value class UserId(val value: String)

data class User(
    val id: UserId,
    val username: String,
    val password: String,
    var active: Boolean
) {
    fun toPublic(): PublicUserDTO {
        return PublicUserDTO(id.value, username)
    }

    fun toPrivate(): PrivateUserDTO {
        return PrivateUserDTO(username, password)
    }
}


