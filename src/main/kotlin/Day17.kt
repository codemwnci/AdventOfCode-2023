import java.io.File

fun main() {
    Day17().puzzle1()
    Day17().puzzle2()
}

class Day17 {
    private val file = File("inputs/day17.txt")
    private val grid = file.readLines().map { it.map { it.digitToInt() } }
    private val target = Point(grid.size-1, grid[0].size-1)

    data class Point(val row:Int, val col:Int) {
        fun move(dir: Dir, distance:Int = 1) = when(dir) {
            Dir.N -> Point(this.row-distance, this.col)
            Dir.S -> Point(this.row+distance, this.col)
            Dir.E -> Point(this.row, this.col+distance)
            Dir.W -> Point(this.row, this.col-distance)
        }
        fun inBounds(grid: List<List<Any>>) = this.row in grid.indices && this.col in grid[0].indices
    }
    enum class Dir {
        N, S, E, W;
        fun opposite() = when(this) {
            N -> S
            S -> N
            E -> W
            W -> E
        }
    }

    fun puzzle1() { seek(1, 3).printAnswer() }
    fun puzzle2() { seek(4, 10).printAnswer() }

    private fun seek(minDist: Int, maxDist:Int): Int {
        data class PathSeekPoint(val p:Point, val cost:Int, val noGoDir:Dir?) : Comparable<PathSeekPoint> {
            override fun equals(other: Any?) = other is PathSeekPoint && p == other.p && noGoDir == other.noGoDir // ignore cost when comparing
            override fun compareTo(other: PathSeekPoint) = this.cost.compareTo(other.cost) // use cost to compare for priority queue
        }

        val queue = priorityQueueOf(PathSeekPoint(Point(0,0), 0, null))
        val visited = mutableSetOf<PathSeekPoint>()
        val costs = mutableMapOf<PathSeekPoint, Int>()

        while (queue.isNotEmpty()) {
            val psp = queue.remove()

            if (psp.p == target) return psp.cost
            if (!visited.contains(psp)) {
                visited.add(psp)

                // no need to continue in the direction we came from (we've already added all those options to the queue)
                // and can't go backwards, so filter out those directions before proceeding
                Dir.entries.filter { dir -> dir != psp.noGoDir && dir != psp.noGoDir?.opposite() }.forEach { dir ->
                    var costInc = 0
                    // make sure we start from 1 regardless of the min-distance because we have to count the cost
                    (1..maxDist).forEach { dist ->
                        val newP = psp.p.move(dir, dist)
                        if (newP.inBounds(grid)) {
                            costInc += grid[newP.row][newP.col]
                            if (dist >= minDist) {
                                val newCost = psp.cost + costInc
                                val newPsp = PathSeekPoint(newP, 0, dir)
                                if (costs.getOrDefault(newPsp, Int.MAX_VALUE) > newCost) {
                                    costs[newPsp] = newCost
                                    queue.add(PathSeekPoint(newP, newCost, dir))
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0 // should never get here
    }
}