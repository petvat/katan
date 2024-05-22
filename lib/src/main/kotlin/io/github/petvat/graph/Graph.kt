package io.github.petvat.graph

class Graph<V, E>(nodes: List<Node<V, E>>) {
    private val nodes: MutableList<Node<V, E>> = mutableListOf()

    constructor() : this(emptyList())

    init {
        this.nodes.addAll(nodes)
    }

    fun add(node: Node<V, E>) {
        nodes.add(node)
    }

    fun addEdge(edgeData: E, node1: Node<V, E>, node2: Node<V, E>, weight: Int) {

        val edge = Edge(edgeData, node1, node2, weight)

        nodes[0].addEdge(edge)
        nodes[1].addEdge(edge)

    }

    fun remove(node: Node<V, E>) {
        nodes.remove(node)
    }

    fun main() {
        // City and road names.

        val graph = Graph<String, String>()

        val n1 = Node<String, String>("Volda")
        val n2 = Node<String, String>("Ørsta")

        graph.add(n1)
        graph.add(n2)

        graph.addEdge("Voldavegen", n1, n2, 3)
    }
}
