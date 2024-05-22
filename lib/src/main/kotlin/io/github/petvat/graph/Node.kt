package io.github.petvat.graph


class Node<V, E>(val data: V) {
    private val edges: MutableList<Edge<V, E>> = mutableListOf()

    fun addEdge(edge: Edge<V, E>) {
        edges.add(edge)
    }

    fun removeEdge(edge: Edge<V, E>) {
        edges.remove(edge)
    }


}
