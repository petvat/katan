package io.github.petvat.katan.shared

import io.github.petvat.katan.shared.model.session.PrivateUserView
import io.github.petvat.katan.shared.model.session.PublicUserView

@JvmInline
value class UserId(val value: String)

data class User(
    val id: UserId,
    val username: String,
    val password: String,
    var active: Boolean
) {
    fun toPublic(): PublicUserView {
        return PublicUserView(id.value, username)
    }

    fun toPrivate(): PrivateUserView {
        return PrivateUserView(id.value, username, password)
    }
}


