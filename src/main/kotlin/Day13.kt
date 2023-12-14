import java.io.File

fun main() {
    Day13().puzzle1()
    Day13().puzzle2()
}

// Not a great attempt today. My first attempt used code from Day 11 to rotate the grid, and then to check symmetry
// across rows. This worked fine for the example, but not for the puzzle input. Whilst searching for some help on
// why it was failing, came a cross a fantastic answer that I lightly adapted for my own solution. This solution
// makes beautiful use of higher order functions to deal with the horizontal and vertical reflections with maximum code reuse
// https://github.com/komu/advent-of-code-2023-kotlin/blob/main/src/Day13.kt
class Day13 {
    private val file = File("inputs/day13.txt")

    fun puzzle1() {
        val grids = file.readLines().split { it.isBlank() }.map { MirrorGrid(it) }
        grids.sumOf { p ->
            100 * p.findHorizontalReflection() + p.findVerticalReflection()
        }.printAnswer()
    }

    fun puzzle2() {
        val grids = file.readLines().split { it.isBlank() }.map { MirrorGrid(it) }
        grids.sumOf { p ->
            val oldV = p.findVerticalReflection()
            val oldH = p.findHorizontalReflection()
            val v = p.findVerticalReflection(disallowed = oldV, smudges = 1)
            val h = p.findHorizontalReflection(disallowed = oldH, smudges = 1)
            100 * h + v
        }.printAnswer()
    }

    private data class MirrorGrid(val rows: List<String>) {

        private fun rowDiff(i: Int, j: Int) = rows[0].indices.count { x -> rows[i][x] != rows[j][x] }
        private fun colDiff(i: Int, j: Int) = rows.indices.count { y -> rows[y][i] != rows[y][j] }

        fun findHorizontalReflection(disallowed: Int = 0, smudges: Int = 0) =
            findReflection(1..<rows.size, disallowed, smudges, ::rowDiff)

        fun findVerticalReflection(disallowed: Int = 0, smudges: Int = 0): Int =
            findReflection(1..<rows[0].length, disallowed, smudges, ::colDiff)

        private fun findReflection(range: IntRange, disallowed: Int, smudges: Int, diff: (Int, Int) -> Int): Int =
            range.firstOrNull { pos -> pos != disallowed && hasReflectionAt(pos, range, smudges, diff) } ?: 0

        private fun hasReflectionAt(pos: Int, range: IntRange, smudges: Int, diff: (Int, Int) -> Int): Boolean {
            var remainingSmudges = smudges

            var i = pos - 1
            var j = pos
            while (i >= 0 && j <= range.last) {
                remainingSmudges -= diff(i--, j++)
                if (remainingSmudges < 0)
                    return false
            }

            return remainingSmudges == 0
        }
    }
}