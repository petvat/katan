package io.github.petvat.katan.shared.model.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The five tradable resources.
 */
enum class Resource { WOOD, ORE, WHEAT, WOOL, BRICK }

/**
 * Immutable count of resources.
 *
 */
@Serializable
data class ResourceMap(
    val wood: Int = 0,
    val ore: Int = 0,
    val wheat: Int = 0,
    val wool: Int = 0,
    val brick: Int = 0
) {
    operator fun get(resource: Resource): Int = when (resource) {
        Resource.WOOD -> wood
        Resource.ORE -> ore
        Resource.WHEAT -> wheat
        Resource.WOOL -> wool
        Resource.BRICK -> brick
    }

    val total: Int get() = wood + ore + wheat + wool + brick

    operator fun plus(other: ResourceMap): ResourceMap = copy(
        wood = wood + other.wood,
        ore = ore + other.ore,
        wheat = wheat + other.wheat,
        wool = wool + other.wool,
        brick = brick + other.brick
    )

    operator fun minus(other: ResourceMap): ResourceMap = copy(
        wood = wood - other.wood,
        ore = ore - other.ore,
        wheat = wheat - other.wheat,
        wool = wool - other.wool,
        brick = brick - other.brick
    )


    /** Gain `amount` of one resource (harvest, trade payout). */
    fun plus(resource: Resource, amount: Int): ResourceMap = when (resource) {
        Resource.WOOD -> copy(wood = wood + amount)
        Resource.ORE -> copy(ore = ore + amount)
        Resource.WHEAT -> copy(wheat = wheat + amount)
        Resource.WOOL -> copy(wool = wool + amount)
        Resource.BRICK -> copy(brick = brick + amount)
    }

    fun minus(resource: Resource, amount: Int): ResourceMap = plus(resource, -amount)


    fun affords(cost: ResourceMap): Boolean =
        wood >= cost.wood && ore >= cost.ore && wheat >= cost.wheat &&
            wool >= cost.wool && brick >= cost.brick

    companion object {
        val EMPTY = ResourceMap()
    }
}
