package io.github.petvat.katan.shared.model.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class Resource {
    WOOD, ORE, WHEAT, WOOL, BRICK, NON_RESOURCE
}


/**
 * TODO: Use this instead because the current [ResourceMap] serialization is messy.
 */
@Serializable
data class ResourceMapData(
    val wood: Int,
    val ore: Int,
    val wheat: Int,
    val wool: Int,
    val brick: Int
)

class ResourceMap(
    wood: Int,
    ore: Int,
    wheat: Int,
    wool: Int,
    brick: Int
) {
    private var resources = hashMapOf(
        Resource.WOOD to wood,
        Resource.ORE to ore,
        Resource.WHEAT to wheat,
        Resource.WOOL to wool,
        Resource.BRICK to brick
    )

    constructor() : this(
        0, 0, 0, 0, 0
    )

    constructor(resourceMap: ResourceMap) : this(
        resourceMap[Resource.WOOD],
        resourceMap[Resource.ORE],
        resourceMap[Resource.WHEAT],
        resourceMap[Resource.WOOL],
        resourceMap[Resource.BRICK]
    )

    operator fun get(resource: Resource): Int {
        return resources.getOrElse(resource) { 0 }
    }

    fun get(): HashMap<Resource, Int> {
        return HashMap(resources)
    }

    fun getAmount(resource: Resource): Int {
        return resources.getOrElse(resource) { 0 }
    }

    // REMOVE
    fun getMap(): HashMap<Resource, Int> {
        return HashMap(resources)
    }

    fun count(): Int {
        return resources.values.sum()
    }

    operator fun plus(other: ResourceMap): Boolean {
        resources.keys.forEach { key ->
            val current = resources[key] ?: 0
            resources[key] = current + (other.get()[key] ?: 0)
        }
        return true
    }

    /**
     * Subtracts a resource map from this resource map
     */
    operator fun minus(other: ResourceMap): Boolean {
        val result = HashMap<Resource, Int>()
        for ((resource, amount) in resources) {
            val otherAmount = other.getAmount(resource)
            val remainingAmount = getAmount(resource) - otherAmount
            if (remainingAmount < 0) {
                return false
            }
            result[resource] = remainingAmount
        }
        resources = result
        return true
    }

    operator fun minus(resource: Resource): Boolean {
        val value = resources[resource]
        if (value != null && value > 0) {
            resources[resource] = value - 1
            return true
        }
        return false
    }

    fun transaction(resource: Resource, amount: Int) {
        val current = resources[resource] ?: 0
        resources[resource] = current + amount
    }

    fun difference(other: ResourceMap): ResourceMap {
        val difference = ResourceMap(this)
        difference.minus(other);
        return difference
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResourceMap

        return resources == other.resources
    }

    override fun hashCode(): Int {
        return resources.hashCode()
    }

}

