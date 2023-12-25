import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import java.io.File

fun main() {
    Day25().puzzle1()
    Day25().puzzle2()
}

class Day25 {
    private val file = File("inputs/day25.txt")

    fun puzzle1() {

        // tried desperately not to use a package to solve today's answer, but in the end gave up and went searching
        // for a decent graph library that implemented StoerWagner MinCut or Karger's algorithm
        // implemented using JGraphT (first Google hit searching "java mincut graph library")
        val graph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        file.readLines().forEach { it.split(": ").let { (from, others) ->
            graph.addVertex(from)
            others.split(" ").forEach { other ->
                graph.addVertex(other)
                graph.addEdge(from, other)
            }
        }}
        // Returns a Set of one side of the cut From: https://jgrapht.org/javadoc-1.0.0/org/jgrapht/alg/StoerWagnerMinimumCut.html
        val side1 = StoerWagnerMinimumCut(graph).minCut().size
        val side2 = graph.vertexSet().size - side1
        (side1 * side2).printAnswer()
    }


    fun puzzle2() {
        "No Part 2. Merry Xmas".printAnswer()
    }
}