package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.protocol.ActionCode
import kotlinx.serialization.Serializable

/**
 * This interface represents class that is applicable as the data field of a request or response message.
 */
@Serializable
sealed interface PayloadDTO

/**
 * This interfaces represents a request or response with a game action.
 *
 * @see [Request]
 * @see [Response]
 */
@Serializable
sealed interface ActionDTO {
    val actionCode: ActionCode
}

/**
 * This is a marker that shows that the implementing class is a DTO of a domain class.
 */
interface DomainDTO

/**
 * Transmittable classes are classes from the domain logic that should be converted into a DTO to avoid exposing sensitive data to users.
 */
interface Transmittable {
    fun fromDomain(): DomainDTO
}
