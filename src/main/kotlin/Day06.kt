import java.io.File

fun main() {
    Day06().puzzle1()
    Day06().puzzle2()
}

class Day06 {
    private val file = File("inputs/day06.txt")

    fun puzzle1() {
        val (times, distances) = file.readLines().map {
            it.substringAfter(": ").trim().split(Regex("\\s+")).map {
                it.trim().toInt()
            }
        }

        times.mapIndexed { index, time ->
            (1..time).count { speed -> speed * (time - speed) > distances[index] }
        }
        .fold(1) { acc, i ->  acc * i }.printAnswer()
    }

    fun puzzle2() {
        val (time, distance) = file.readLines().map {
            it.substringAfter(": ").replace(" ", "").toLong()
        }
        (1..time).count { speed -> speed * (time - speed) > distance }.printAnswer()
    }
}