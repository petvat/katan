package io.github.petvat.katan.shared.protocol

import com.google.gson.annotations.SerializedName

enum class MessageType(val key: String) {

    @SerializedName("CHAT")
    CHAT("CHAT"),

    @SerializedName("ACTION")
    ACTION("ACTION"),

    @SerializedName("JOIN")
    JOIN("JOIN"),

    @SerializedName("CREATE")
    CREATE("CREATE"),

    @SerializedName("INIT")
    INIT("INIT"),

    @SerializedName("LOGIN")
    LOGIN("LOGIN"),

    @SerializedName("GET_GROUPS")
    GET_GROUPS("GET_GROUPS"),

    @SerializedName("ACK")
    ACK("ACK")

    ;

    companion object {
        const val IDENTIFIER = "MessageType"
    }
}
