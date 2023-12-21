import java.io.File
import kotlin.math.abs

fun main() {
    Day21().puzzle1()
    Day21().puzzle2()
}

class Day21 {
    private val file = File("inputs/day21.txt")
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
        val map = file.readLines().map { it.toList() }
        val start = map.flatMapIndexed { row, chars -> chars.mapIndexedNotNull { col, c -> if (c == 'S') Point(row, col) else null } }.first()

        val count = (1..64).fold(listOf(start)) { paths, _ ->
            paths.flatMap { it.neighbours().filter { it.inBounds(map) && map[it.row][it.col] == '.' } }.distinct()
        }
        .count() + 1

        count.printAnswer()
    }

    // Part 1 was easy. Part 2 was a struggle (brute for was not going to cut it, unfortunately.
    // I ended up getting a lot of help from Jonathan Paulson's solution. His YouTube video explainer was very useful.
    // https://www.youtube.com/watch?v=C2dmxCGGH1s
    // https://github.com/jonathanpaulson/AdventOfCode/blob/master/2023/21.py
    // Reimplemented his solution in Kotlin (and specifically for part 2 only to slightly simplify)
    fun puzzle2() {
        data class TilePoint(val tileRow:Int, val tileCol:Int, val point:Point)

        var ans = 0L
        val targetSteps = 26_501_365L
        val map = file.readLines().map { it.toList() }
        val start = map.flatMapIndexed { row, chars -> chars.mapIndexedNotNull { col, c -> if (c == 'S') Point(row, col) else null } }.first()
        val gridSize = map.size

        fun findD(): MutableMap<TilePoint, Long> {
            val d = mutableMapOf<TilePoint, Long>()
            val queue = ArrayDeque<Pair<TilePoint, Long>>().also { it.add(TilePoint(0,0,start) to 0) }

            while (queue.isNotEmpty()) {
                val (tp, dist) = queue.removeFirst()
                var (tr, tc, _) = tp
                var (r, c) = tp.point

                // if moved Point goes beyond the boundary of the tile, move to adjacent tile
                if (r < 0) {
                    tr -= 1
                    r += gridSize
                }
                if (r >= gridSize) {
                    tr += 1
                    r -= gridSize
                }
                if (c < 0) {
                    tc -= 1
                    c += gridSize
                }
                if (c >= gridSize) {
                    tc += 1
                    c -= gridSize
                }

                val newTP = TilePoint(tr, tc, Point(r, c))
                if (newTP.point.inBounds(map) && map[newTP.point.row][newTP.point.col] != '#' && !d.contains(newTP) && abs(tr) <=3 && abs(tc) <=3) {
                    d[newTP] = dist
                    newTP.point.neighbours().forEach {
                        queue.add(TilePoint(tr, tc, it) to dist+1)
                    }
                }
            }
            return d
        }

        val solve = mutableMapOf<Pair<Long, Long>, Long>()
        fun solve(d: Long, v:Long):Long {
            val amt = (targetSteps-d) / gridSize
            solve[d to v]?.let { return it }

            var ret = 0L
            (1..amt).forEach { x ->
                if (d+gridSize*x <= targetSteps && (d+gridSize*x)%2 == targetSteps%2) {
                    ret += if (v==2L) x+1 else 1
                }
            }
            solve[d to v] = ret
            return ret
        }

        val distances = findD()
        map.forEachIndexed { row, chars -> chars.forEachIndexed { col, _ ->
            val p = Point(row,col)
            if (distances.contains(TilePoint(0,0, p))) {
                val tilesOut = -2..2
                tilesOut.forEach { tr -> tilesOut.forEach { tc ->
                    val dist = distances[TilePoint(tr, tc, p)]!!

                    if (dist % 2 == targetSteps % 2 && dist <= targetSteps) {
                        ans += 1
                    }
                    val edges = listOf(tilesOut.first, tilesOut.last)
                    if (tr in edges && tc in edges) {
                        ans += solve(dist, 2)
                    }
                    else if (tr in edges || tc in edges) {
                        ans += solve(dist, 1)
                    }
                }}
            }
        }}
        ans.printAnswer()
    }
}