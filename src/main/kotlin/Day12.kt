import java.io.File
import java.lang.Exception

fun main() {
    //Day12().puzzle1()
    Day12().puzzle1a()
    Day12().puzzle2()
}

class Day12 {
    private val file = File("inputs/day12.txt")
    private val conditions = file.readLines().map {
        it.split(" ").let {
            it[0] to it[1].split(",").map { it.toInt() }
        }
    }

    fun puzzle1() {
        conditions.sumOf {
            val (row, engineerRecord) = it

            // find all combinations
            val combinations = mutableListOf<String>()
            val queue = mutableListOf(row)
            while (queue.isNotEmpty()) {
                val item = queue.removeFirst()
                if (!item.contains("?")) {
                    combinations.add(item)
                }
                else {
                    queue.add(item.replaceFirst("?", "#"))
                    queue.add(item.replaceFirst("?", "."))
                }
            }
            // count how many combinations match the engineer's records
            combinations.count {
                val contiguousGroups = mutableListOf<Int>()

                var count = 0
                it.forEach { c ->
                    if (c == '#') count++
                    else if (c == '.' && count != 0) {
                        contiguousGroups.add(count)
                        count = 0
                    }
                }

                if (count > 0) contiguousGroups.add(count)

                contiguousGroups == engineerRecord
            }
        }.printAnswer()
    }

    // Went back and re-wrote 1a to use the same functions as 2. Finishes far quicker as a result
    fun puzzle1a() {
        conditions.sumOf { solve(it.first, it.second) }.printAnswer()
    }

    // Part 2 had no hope of finishing in time, if using the brute force approach used in part 1
    // Caching was always going to be the answer, but needed a lot of inspiration from Reddit and in particular, Kroppeb
    // https://github.com/Kroppeb/AdventOfCodeSolutions2/blob/master/solutions/src/solutions/y2023/day%2012.kt

    fun puzzle2() {
        conditions.sumOf {
            val (rawRow, rawEngineerRecord) = it

            val row = List(5) { rawRow }.joinToString("?")
            val engineerRecord = List(5) { rawEngineerRecord }.reduce { acc, ints -> acc+ints }

            solve(row, engineerRecord)
       }.printAnswer()
    }

    private val cache = mutableMapOf<Pair<List<Int>, String>, Long>()

    private fun solve(springStates: String, record: List<Int>): Long {
        return if (springStates.isEmpty()) { if (record.isEmpty()) 1L else 0L }
        else when(springStates.first()) {
                '.' -> solve(springStates.drop(1), record)
                '#' -> hs(record, springStates).also{cache[record to springStates] = it}
                '?' -> solve(springStates.drop(1), record) + cache.getOrPut(record to springStates) {hs(record, springStates)}
                else -> throw Exception("should not happen")
            }
    }

    private fun hs(record: List<Int>, springStates: String) : Long {
        if (record.isEmpty()) return 0L

        val x = record[0]
        if (springStates.length < x) return 0L
        if (springStates.substring(0..< x).any { it == '.' }) return 0L
        if (springStates.length == x) { return if (record.size == 1) 1L else 0L }
        if (springStates[x] == '#') return 0L

        return solve(springStates.drop(x + 1), record.drop(1))
    }
}