import java.io.File

fun main() {
    Day16().puzzle1()
    Day16().puzzle2()
}

class Day16 {
    private val file = File("inputs/day16.txt")
    data class Point(val x:Int, val y:Int) {
        fun move(dir: Dir) = when(dir) {
            Dir.N -> Point(this.x, this.y-1)
            Dir.S -> Point(this.x, this.y+1)
            Dir.E -> Point(this.x+1, this.y)
            Dir.W -> Point(this.x-1, this.y)
        }
    }
    enum class Dir {
        N, S, E, W
    }

    private val grid = file.readLines().map { it.toList() }

    fun puzzle1() {
        countEnergised(Point(0,0) to Dir.E).printAnswer()
    }

    fun puzzle2() {
        val startPosList = mutableListOf<Pair<Point, Dir>>()
        grid.indices.forEach {
            startPosList.add(Point(0, it) to Dir.E)
            startPosList.add(Point(grid[0].size-1, it) to Dir.E)
        }
        grid[0].indices.forEach {
            startPosList.add(Point(it, 0) to Dir.S)
            startPosList.add(Point(it, grid.size-1) to Dir.N)
        }

        startPosList.maxOf { countEnergised(it) }.printAnswer()
    }

    private fun countEnergised(start: Pair<Point, Dir>): Int {
        val energised = mutableSetOf<Point>()
        val toProcess = ArrayDeque<Pair<Point, Dir>>().also { it.add(start) }
        val hasProcessed = mutableListOf<Pair<Point, Dir>>()

        while (toProcess.isNotEmpty()) {
            val pair = toProcess.removeFirst()
            val (pos, dir) = pair

            if (!hasProcessed.contains(pair) && pos.x in grid[0].indices && pos.y in grid.indices) {
                energised.add(pos)
                hasProcessed.add(pair)

                val gridPos = grid[pos.y][pos.x]
                if (gridPos == '.' || (gridPos == '-' && (dir == Dir.W || dir == Dir.E) || (gridPos == '|' && (dir == Dir.N || dir == Dir.S)))) {
                    toProcess.add(pos.move(dir) to dir)
                } else if (gridPos == '|' && (dir == Dir.E || dir == Dir.W)) { //split
                    toProcess.add(pos.move(Dir.N) to Dir.N)
                    toProcess.add(pos.move(Dir.S) to Dir.S)
                } else if (gridPos == '-' && (dir == Dir.N || dir == Dir.S)) { //split
                    toProcess.add(pos.move(Dir.E) to Dir.E)
                    toProcess.add(pos.move(Dir.W) to Dir.W)
                }
                else { // only 90 degree reflection now
                    if (gridPos == '/') {
                        when (dir) {
                            Dir.N -> toProcess.add(pos.move(Dir.E) to Dir.E)
                            Dir.S -> toProcess.add(pos.move(Dir.W) to Dir.W)
                            Dir.E -> toProcess.add(pos.move(Dir.N) to Dir.N)
                            Dir.W -> toProcess.add(pos.move(Dir.S) to Dir.S)
                        }
                    }
                    else {
                        when (dir) {
                            Dir.N -> toProcess.add(pos.move(Dir.W) to Dir.W)
                            Dir.S -> toProcess.add(pos.move(Dir.E) to Dir.E)
                            Dir.E -> toProcess.add(pos.move(Dir.S) to Dir.S)
                            Dir.W -> toProcess.add(pos.move(Dir.N) to Dir.N)
                        }
                    }
                }
            }
        }
        return energised.size
    }
}