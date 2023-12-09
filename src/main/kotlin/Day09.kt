import java.io.File

fun main() {
    Day09().puzzle1()
    Day09().puzzle2()
}

class Day09 {
    private val file = File("inputs/day09.txt")

    fun puzzle1() {
        fun nextInSeq(ints: List<Int>) :Int {
            val diffs = ints.zipWithNext { a, b -> b-a }
            return if (diffs.all { it == 0 }) ints.last() else ints.last() + nextInSeq(diffs)
        }

        file.readLines().sumOf {
            nextInSeq( it.split(" ").map { num -> num.toInt() } )
        }.printAnswer()
    }

    fun puzzle2() {
        fun nextInSeqLeft(ints: List<Int>) :Int {
            val diffs = ints.zipWithNext { a, b -> b-a }
            return if (diffs.all { it == 0 }) ints.first() else ints.first() - nextInSeqLeft(diffs)
        }

        file.readLines().sumOf {
            nextInSeqLeft(it.split(" ").map { num -> num.toInt() })
        }.printAnswer()
    }
}