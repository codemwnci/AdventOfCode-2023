import java.io.File

fun main() {
    Day03().puzzle1()
    Day03().puzzle2()
}

class Day03 {
    private val file = File("inputs/day03.txt")
    data class YX(val y:Int, val x:Int)

    private val numbers = mutableListOf<Pair<YX, Int>>()
    private val starList = mutableListOf<YX>()
    private val grid = file.readLines().also {
        it.forEachIndexed { y, str ->
            var first = -1
            var last = -1
            str.forEachIndexed { idx, c ->
                if (c.isDigit() && first < 0) first = idx
                else if (!c.isDigit() && first >= 0) {
                    last = idx - 1
                } else if (idx == str.length - 1 && c.isDigit()) {
                    last = idx
                }

                if (first >= 0 && last >= 0) {
                    numbers.add(YX(y, first) to str.substring(first, last + 1).toInt())
                    first = -1
                    last = -1
                }

                if (c == '*') starList.add(YX(y, idx)) // find all stars at the same time
            }
        }
    }

    private fun neighbours(nums: Pair<YX, Int>): List<YX> {
        val yRange = nums.first.y - 1..nums.first.y + 1
        val xRange = nums.first.x - 1..nums.first.x + nums.second.toString().length

        return yRange.map { y -> xRange.map { x -> YX(y, x) } }.flatten().filter {
            // no need to worry about neighbours including self, because we are not testing for numerics
            // but ensure we stay inside boundary
            it.y in 0 until grid.size && it.x in 0 until grid[0].length
        }
    }

    fun puzzle1() {
        val sum = numbers.filter { nums ->
            neighbours(nums).any {
                val c = grid[it.y].elementAt(it.x)
                !c.isDigit() && c != '.'
            }
        }.sumOf { it.second }.printAnswer()
    }

    fun puzzle2() {
        val sumOfRatios = starList.sumOf { star ->
            val nextToStar = numbers.filter { nums ->  neighbours(nums).any { yx -> yx == star} } // filter out any that aren't adjacent to this star

            if (nextToStar.size == 2) { nextToStar[0].second * nextToStar[1].second } // if exactly 2 adjacent, return the gear ratio, else 0
            else 0
        }.printAnswer()
    }
}