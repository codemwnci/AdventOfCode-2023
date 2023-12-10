import java.io.File
import java.lang.Exception

fun main() {
    Day10().puzzle1()
    Day10().puzzle2()
}

class Day10 {
    private val file = File("inputs/day10.txt")
    data class Pipe(val pos:Point, val type:Char)
    data class Point(val x:Int, val y:Int) {
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
        fun neighbours(boundary: Point) = listOf(Point(x+1, y), Point(x-1, y), Point(x, y+1), Point(x, y-1)).filter { it.x >= 0 && it.x <= boundary.x && it.y >= 0 && it.y <= boundary.y }
        fun getAdjacentSides() = listOf(Point(x, y - 1), Point(x - 1, y), Point(x + 1, y), Point(x, y + 1))
        fun getAdjacent() = (-1 .. 1).map { xx -> (-1 .. 1).map { yy ->  Point(x+xx, y+yy) } }.flatten().filter { it != this }
    }
    private val UP = Point(0, -1)
    private val DOWN = Point(0, 1)
    private val LEFT = Point(-1, 0)
    private val RIGHT = Point(1, 0)

    lateinit var start: Point
    val grid = file.readLines().mapIndexed { y, line -> line.mapIndexed { x, c ->
        if (c == 'S') start = Point(x, y)
        Pipe(Point(x, y), c)
    }}

    fun puzzle1() {
        // this hardcodes the first point direction - should ideally calculate
        val path = mutableListOf<Point>(start, Point(start.x-1, start.y))
        while(true) {
            val next = getNextPipePoint(path, grid)

            if (next == start) break
            path += next
        }

        (path.size / 2).printAnswer()
    }

    private fun getNextPipePoint(path1: List<Point>, grid: List<List<Pipe>>): Point {
        val head = path1.takeLast(2)
        val diffx = head[1].x - head[0].x
        val diffy = head[1].y - head[0].y
        val curr = grid[head[1].y][head[1].x]

        return if (diffx == -1 && curr.type == 'L') curr.pos + UP
            else if (diffx == -1 && curr.type == 'F') curr.pos + DOWN
            else if (diffx == -1 && curr.type == '-') curr.pos + LEFT
            else if (diffx == 1 && curr.type == 'J') curr.pos + UP
            else if (diffx == 1 && curr.type == '7') curr.pos + DOWN
            else if (diffx == 1 && curr.type == '-') curr.pos + RIGHT
            else if (diffy == -1 && curr.type == '7') curr.pos + LEFT
            else if (diffy == -1 && curr.type == 'F') curr.pos + RIGHT
            else if (diffy == -1 && curr.type == '|') curr.pos + UP
            else if (diffy == 1 && curr.type == 'J') curr.pos + LEFT
            else if (diffy == 1 && curr.type == 'L') curr.pos + RIGHT
            else if (diffy == 1 && curr.type == '|') curr.pos + DOWN
            else throw Exception("This should never happen ${path1.size}")
    }

    // needed help on part 2, so I did what most others appeared to do, which
    // was to expand the grid so each point was a 3x3 tile, and then fill in the
    // relevant points based on the specific pipe, then do a whole map parse to
    // 'flood' the map with water (from the origin).
    // The solution then is to just count the original grid to see how many are not filled with water
    fun puzzle2() {
        val path = mutableListOf<Point>(start, Point(start.x-1, start.y))
        while(true) {
            val next = getNextPipePoint(path, grid)

            if (next == start) break
            path += next
        }

        val pipes = mutableMapOf(
            'S' to listOf(UP, RIGHT, DOWN, LEFT),
            '|' to listOf(UP, DOWN),
            '-' to listOf(RIGHT, LEFT),
            'L' to listOf(UP, RIGHT),
            'J' to listOf(UP, LEFT),
            '7' to listOf(DOWN, LEFT),
            'F' to listOf(DOWN, RIGHT)
        )

        val expandedGrid = mutableMapOf<Point, Char>()
        grid.flatten().forEach { (point, char) ->
            val expandedPoint = Point(point.x * 3, point.y * 3)
            expandedGrid[expandedPoint] = if (char != '.' && point in path) '#' else '.'
            expandedPoint.getAdjacent().forEach { expandedGrid[it] = '.' }
            if (point in path) pipes[char]!!.forEach { expandedGrid[expandedPoint+it] = '#' }
        }

        val water = mutableListOf(Point(0,0))
        while (water.isNotEmpty()) {
            val current = water.removeFirst()
            expandedGrid[current] = '='
            water += current.getAdjacentSides().filter { expandedGrid[it] == '.' && it !in water }
        }

        grid.flatten().count { expandedGrid[Point(it.pos.x * 3, it.pos.y * 3)] == '.' }.printAnswer()
    }
}