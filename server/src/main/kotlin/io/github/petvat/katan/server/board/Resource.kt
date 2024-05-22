package io.github.petvat.katan.server.board

enum class Resource {
    WOOD, ORE, WHEAT, WOOL, BRICK
}

/**
 * Logic to wrap resources. Hopefully makes it easier to work with.
 *
 */

class ResourceMap(
    wood: Int,
    ore: Int,
    wheat: Int,
    wool: Int,
    brick: Int
) {
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
}


