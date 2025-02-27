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

class ResourceMap2(
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

    private constructor(resourceMap: ResourceMap2) : this(
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

    fun count(): Int {
        return resources.values.sum()
    }

    operator fun plus(other: ResourceMap2): Boolean {
        resources.keys.forEach { key ->
            val current = resources[key] ?: 0
            resources[key] = current + (other.get()[key] ?: 0)
        }
        return true
    }

    /**
     * Subtracts a resource map from this resource map
     */
    operator fun minus(other: ResourceMap2): Boolean {
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

    fun transaction(resource: Resource, amount: Int) {
        val current = resources[resource] ?: 0
        resources[resource] = current + amount
    }

    fun difference(other: ResourceMap2): ResourceMap2 {
        val difference = ResourceMap2(this)
        difference.minus(other);
        return difference
    }
}

fun ResourceMap2.fromDomain() = ResourceMapData(
    this[Resource.WOOD],
    this[Resource.ORE],
    this[Resource.WHEAT],
    this[Resource.WOOL],
    this[Resource.BRICK]
)

/**
 * Logic to wrap resources. Hopefully makes it easier to work with.
 *
 * TODO: Fix weird serialization logic
 */
@Serializable
class ResourceMap(
    private val wood: Int,
    private val ore: Int,
    private val wheat: Int,
    private val wool: Int,
    private val brick: Int
) {
    @Transient
    private var resources: HashMap<Resource, Int> = hashMapOf(
        Resource.WOOD to wood,
        Resource.ORE to ore,
        Resource.WHEAT to wheat,
        Resource.WOOL to wool,
        Resource.BRICK to brick
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

    fun getMap(): HashMap<Resource, Int> {
        return HashMap(resources)
    }


    fun count(): Int {
        return resources.values.sum()
    }

    /**
     * Simple and can be used for all.
     */
    operator fun plus(other: ResourceMap): Boolean {
        resources.keys.forEach { key ->
            val current = resources[key] ?: 0
            resources[key] = current + (other.getMap()[key] ?: 0)
        }
        return true
    }

    /**
     * Subtracts a resource map from this resource map *if* the subtraction results in no negative values.
     *
     * @return true if the subtraction could be performed.
     */
    operator fun minus(other: ResourceMap): Boolean {
        val result = HashMap<Resource, Int>()
        for ((resource, _) in resources) {
            val remainingAmount = this[resource] - other[resource]
            if (remainingAmount < 0) {
                return false
            }
            result[resource] = remainingAmount
        }
        resources = result
        return true
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


