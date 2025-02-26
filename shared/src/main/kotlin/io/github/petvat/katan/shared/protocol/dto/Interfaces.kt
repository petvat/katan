package io.github.petvat.katan.shared.protocol.dto

import kotlinx.serialization.Serializable

/**
 * This is a marker that shows that the implementing class is a DTO of a domain class.
 *
 * NOTE: No point in this anymore.
 */
interface DomainDTO

/**
 * Transmittable classes are classes from the domain logic that should be converted into a DTO to avoid exposing sensitive data to users.
 */
interface Transmittable {
    fun fromDomain(): DomainDTO
}
