import java.io.File

fun main() {
    Day01().puzzle1()
    Day01().puzzle2()
}

class Day01 {
    private val file = File("inputs/day01.txt")

    fun puzzle1() {
        println(file.readLines().map { it.filter { it.isDigit() } }.sumOf { (it.first() + "" + it.last()).toInt() })
    }

    fun puzzle2() {
        val res = file.readLines().map {str ->
            str.windowed(5, 1, true) {
               when {
                   it[0].isDigit() -> it[0].toString()
                   it.startsWith("one") -> "1"
                   it.startsWith("two") ->  "2"
                   it.startsWith("three") -> "3"
                   it.startsWith("four") -> "4"
                   it.startsWith("five") -> "5"
                   it.startsWith("six") -> "6"
                   it.startsWith("seven") -> "7"
                   it.startsWith("eight") -> "8"
                   it.startsWith("nine") -> "9"
                   else -> ""
               }
            }
        }
        .map {
            it.filter { c -> c.isNotBlank() }
        }
        .sumOf { (it.first() + it.last()).toInt() }

        println(res)
    }
}