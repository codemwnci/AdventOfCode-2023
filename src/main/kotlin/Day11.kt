import java.io.File
import kotlin.math.*

fun main() {
    Day11().puzzle1()
    Day11().puzzle2()
}

class Day11 {
    private val file = File("inputs/day11.txt")

    data class Point(val x:Int, val y:Int)
    private fun isSpaceBetween(start:Int, end:Int, toCompare: Int) = toCompare in min(start, end) .. max(start, end)

    fun puzzle1() {
        val universe = file.readLines().map { it.toMutableList() }.toMutableList()
        val emptyRows = (0..< universe.size).filter { universe[it].all { it == '.' } }
        val emptyCols = (0..< universe[0].size).filter { col -> (0..< universe.size).map { r -> universe[r][col] }.all { it == '.' } }

        val galaxies = universe.flatMapIndexed  { row, chars ->
            chars.mapIndexedNotNull { col, c -> if (c == '#') Point(col, row) else null }
        }

        val pairs = galaxies.combinations(2)
        pairs.sumOf {
            val distance = abs(it[0].x-it[1].x) + abs(it[0].y-it[1].y)
            val rowSpace = emptyRows.count { row-> isSpaceBetween(it[0].y, it[1].y, row) }
            val colSpace = emptyCols.count { col -> isSpaceBetween(it[0].x, it[1].x, col)  }

            distance + rowSpace + colSpace
        }.printAnswer()
    }

    fun puzzle2() {
        val universe = file.readLines().map { it.toMutableList() }.toMutableList()
        val emptyRows = (0..< universe.size).filter { universe[it].all { it == '.' } }
        val emptyCols = (0..< universe[0].size).filter { col -> (0..< universe.size).map { r -> universe[r][col] }.all { it == '.' } }

        val galaxies = universe.flatMapIndexed  { row, chars ->
            chars.mapIndexedNotNull { col, c -> if (c == '#') Point(col, row) else null }
        }

        val pairs = galaxies.combinations(2)
        pairs.sumOf {
            val distance = abs(it[0].x-it[1].x) + abs(it[0].y-it[1].y)
            val rowSpace = emptyRows.count { row-> isSpaceBetween(it[0].y, it[1].y, row) }
            val colSpace = emptyCols.count { col -> isSpaceBetween(it[0].x, it[1].x, col)  }

            distance + rowSpace*999_999L + colSpace*999_999L
        }.printAnswer()
    }
}