
package io.github.petvat.graph

/**
 * V: Generic node data
 * E: Generic egde data
 */
data class Edge<V, E> (val data: E,val node1: Node<V, E>,val node2: Node<V, E>,var weight: Int) {


    fun getAdjacentNode(node: Node<V, E>): Node<V, E> {
        if (node1 != node && node2 != node) {
            throw IllegalArgumentException()
        }
        return if (node == node1) node1 else node2
    }
}

fun main() {

}
