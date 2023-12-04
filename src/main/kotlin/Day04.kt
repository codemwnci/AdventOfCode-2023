import java.io.File

fun main() {
    Day04().puzzle1()
    Day04().puzzle2()
}

class Day04 {
    private val file = File("inputs/day04.txt")
    private val cards = file.readLines().map {
        it.substringAfter(": ").split(" | ").map {
            it.windowed(3, 3, true).map { it.trim().toInt() }
        }
    }

    fun puzzle1() {
        cards.sumOf { (winners, mine) ->
            val count = mine.intersect(winners).count()
            if (count == 0) 0 else Math.pow(2.0, count - 1.0).toInt()
        }.printAnswer()
    }

    fun puzzle2() {
        val copies = IntArray(cards.size) { 1 }
        cards.forEachIndexed { idx, (winners, mine) ->
            val count = mine.intersect(winners).count()
            (idx + 1..idx + count).forEachIndexed { _, i ->
                if (i < copies.size) copies[i] += copies[idx]
            }
        }
        copies.sum().printAnswer()
    }
}