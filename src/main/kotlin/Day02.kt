import java.io.File

fun main() {
    Day02().puzzle1()
    Day02().puzzle2()
}

class Day02 {
    private val file = File("inputs/day02.txt")

    data class Game(val id: Int, val rgbs:List<IntArray>)

    private val input = file.readLines().map {
        val id = it.drop(5).takeWhile { it != ':' }.toInt() // id
        val rgbs = it.substringAfter(": ").split("; ").map { cubes ->
            var r=0
            var g=0
            var b=0

            cubes.split(", ").forEach { cube ->
                val num = cube.substringBefore(" ").toInt()
                if (cube.endsWith("blue")) b+=num
                else if (cube.endsWith("green")) g+=num
                else if (cube.endsWith("red")) r+=num
            }

            arrayOf(r, g, b).toIntArray()
        }
        Game(id, rgbs)
    }

    fun puzzle1() {
        println(input.filter { it.rgbs.all { rgb -> rgb[0] <= 12 && rgb[1] <= 13 && rgb[2] <= 14} }.sumOf { it.id })
    }

    fun puzzle2() {
        println(input.sumOf { game -> game.rgbs.maxOf { it[0] } * game.rgbs.maxOf { it[1] } * game.rgbs.maxOf { it[2] } })
    }
}