import java.io.File

fun main() {
    Day14().puzzle1()
    Day14().puzzle2()
}

class Day14 {
    private val file = File("inputs/day14.txt")

    fun puzzle1() {
        val grid = file.readLines().map { it.toMutableList() }
        tiltToTop(grid)

        score(grid).printAnswer()
    }

    fun puzzle2() {
        var grid = file.readLines().map { it.toMutableList() }

        val gridHistory = HashMap<String, Int>()
        val target = 1_000_000_000
        var curr = 0

        while (curr < target) {
            curr++
            repeat(4) {
                tiltToTop(grid)
                grid = rotate(grid)
            }

            // can't brute for a billion cycles, so look for cycles to skip much of the processing
            val gridString = grid.toString()
            if (gridHistory.containsKey(gridString)) {
                val cycleSize = curr - gridHistory[gridString]!!
                val amount = (target-curr) / cycleSize
                curr += amount * cycleSize
            }
            gridHistory[gridString] = curr
        }

        score(grid).printAnswer()
    }

    private fun tiltToTop(g: List<MutableList<Char>>) {
        var hasMoved = true
        while (hasMoved) {
            hasMoved = false
            g.zipWithNext { row1, row2 ->
                row1.forEachIndexed { index, c ->
                    if (c == '.' && row2[index] == 'O') {
                        row1[index] = 'O'
                        row2[index] = '.'
                        hasMoved = true
                    }
                }
            }
        }
    }
    private fun score(g: List<MutableList<Char>>) = g.foldIndexed(0) { index, sum, row ->
        sum + row.count { it == 'O' } * (g.size - index)
    }
    private fun rotate(g: List<MutableList<Char>>) =
        (0..< g[0].size).map { col -> (0..< g.size).map { r -> g[g.size-1-r][col] }.toMutableList() }

}