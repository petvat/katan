package io.github.petvat.katan.shared.util


/**
 * Returns a list of the mapped values from a given list of keys.
 * Requires that all keys in the input are present in the map that is being operated on.
 *
 * @param keys List of keys that is required
 * @return List of values
 */
fun <K, V> Map<K, V>.requireValues(keys: List<K>): List<V> {
    return keys.map { key ->
        requireNotNull(this[key]) { "Value for $key is missing." }
    }
}
