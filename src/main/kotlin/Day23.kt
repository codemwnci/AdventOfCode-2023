import java.io.File
import kotlin.math.max

fun main() {
    Day23().puzzle1()
    Day23().puzzle2()
}

class Day23 {
    private val file = File("inputs/day23.txt")
    data class Point(val row:Int, val col:Int) {
        fun move(dir: Dir, distance:Int = 1) = when(dir) {
            Dir.N -> Point(this.row-distance, this.col)
            Dir.S -> Point(this.row+distance, this.col)
            Dir.E -> Point(this.row, this.col+distance)
            Dir.W -> Point(this.row, this.col-distance)
        }
        fun inBounds(grid: List<List<Any>>) = this.row in grid.indices && this.col in grid[0].indices
        fun neighbours() = listOf(move(Dir.N), move(Dir.E), move(Dir.S), move(Dir.W))
    }
    enum class Dir { N, S, E, W }

    fun puzzle1() {
        val grid = file.readLines().map { it.toList() }
        val start = Point(0, 1)

        val validPaths = mutableListOf<List<Point>>()
        val queue = ArrayDeque<List<Point>>().also { it.add(listOf(start)) }

        while(queue.isNotEmpty()) {
            val path = queue.removeFirst()
            if (path.last() == Point(grid.size-1, grid.size-2)) {
                validPaths.add(path)
            }
            else {
                val next = path.last().neighbours().filter { it.inBounds(grid) && grid[it.row][it.col] != '#' && !path.contains(it) }
                next.forEach {
                    when (grid[it.row][it.col]) {
                        '.' -> queue.addFirst(path + it)
                        '>' -> if (path.last() == it.move(Dir.W)) queue.addFirst(path + it + it.move(Dir.E))
                        '<' -> if (path.last() == it.move(Dir.E)) queue.addFirst(path + it + it.move(Dir.W))
                        '^' -> if (path.last() == it.move(Dir.S)) queue.addFirst(path + it + it.move(Dir.N))
                        'v' -> if (path.last() == it.move(Dir.N)) queue.addFirst(path + it + it.move(Dir.S))
                    }
                }
            }
        }

        validPaths.maxOf { it.size - 1 }.printAnswer()
    }


    // tried a brute force whilst writing the edge contraction optimisation approach
    // didn't look like brute force didn't complete before the optimisation was ready, so gave up and used
    // this instead.
    fun puzzle2() {
        val grid = file.readLines().map { it.toList() }
        val start = Point(0, grid.first().indexOf('.'))
        val end = Point(grid.size-1, grid.last().indexOf('.'))

        // find all the junctions (vertices)
        val junctions = grid.flatMapIndexed { row, chars -> chars.mapIndexedNotNull { col, c ->
            val neighbours = Point(row, col).neighbours().filter { it.inBounds(grid) && grid[it.row][it.col] != '#' }
            if (c != '#' && neighbours.size > 2) Point(row, col) else null
        } }.toMutableList()

        junctions.add(0, start)
        junctions.add(end)

        // from the junctions, calculate the path lengths of the paths leading from each junction to the next junction
        val edges = mutableMapOf<Point, List<Pair<Point, Int>>>()
        junctions.forEach { junction ->
            val visited = mutableSetOf<Point>()
            val queue = ArrayDeque<Pair<Point, Int>>()
            queue.add(junction to 0)

            val paths = mutableListOf<Pair<Point, Int>>()
            while (queue.isNotEmpty()) {
                val (point, distance) = queue.removeFirst()
                if (!visited.contains(point)) {
                    visited.add(point)
                    if (junctions.contains(point) && point != junction) {
                        paths.add(point to distance)
                    }
                    else {
                        point.neighbours().filter { it.inBounds(grid) && grid[it.row][it.col] != '#' }.forEach {
                            queue.add(it to distance+1)
                        }
                    }
                }
            }
            edges[junction] = paths
        }

        var currMax = 0
        val visited = List(grid.size) { MutableList(grid[0].size) { false } }
        // now calculate using a depth first search
        fun dfs(p:Point, d:Int) {
            if (visited[p.row][p.col]) return

            visited[p.row][p.col] = true
            if (p == end) currMax = max(currMax, d)
            edges[p]?.forEach { (next, distToNext) ->
                dfs(next, d+distToNext)
            }
            visited[p.row][p.col] = false
        }

        dfs(start, 0)
        currMax.printAnswer()
    }
}