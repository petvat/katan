package io.github.petvat.katan.shared.model.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class Resource {
    WOOD, ORE, WHEAT, WOOL, BRICK, NON_RESOURCE
}

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
        resourceMap.getAmount(Resource.WOOD),
        resourceMap.getAmount(Resource.ORE),
        resourceMap.getAmount(Resource.WHEAT),
        resourceMap.getAmount(Resource.WOOL),
        resourceMap.getAmount(Resource.BRICK)
    )

    operator fun get(resource: Resource): Int? {
        return resources[resource]
    }

    fun get(): HashMap<Resource, Int> {
        return HashMap(resources)
    }

    fun getAmount(resource: Resource): Int {
        return resources.getOrElse(resource) { 0 }
    }

    /**
     * Simple and can be used for all.
     *
     */
    fun plus(other: ResourceMap): Boolean {
        resources.keys.forEach { key ->
            val current = resources[key] ?: 0
            resources[key] = current + (other.get()[key] ?: 0)
        }
        return true
    }

    /**
     * Subtracts a resource map from this resource map
     */
    fun minus(other: ResourceMap): Boolean {
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


