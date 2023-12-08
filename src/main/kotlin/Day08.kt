import java.io.File

fun main() {
    Day08().puzzle1()
    Day08().puzzle2()
}

class Day08 {
    private val file = File("inputs/day08.txt")

    private val seq = file.readLines().first().toList()
    private val routeMap = mutableMapOf<String, Pair<String, String>>().also { map ->
        file.readLines().drop(2).forEach {
            val pos = it.substring(0, 3)
            val left = it.substring(7, 10)
            val right = it.substring(12, 15)

            map[pos] = left to right
        }
    }

    fun puzzle1() {
        var position = "AAA"
        var steps = 0
        while (position != "ZZZ") {
            val leftOrRight = seq[steps % seq.size]
            steps++
            position = if (leftOrRight == 'L') routeMap[position]!!.first else routeMap[position]!!.second
        }
        println(steps)
    }

    fun puzzle2() {
        val positions = routeMap.keys.toList().filter { it.endsWith("A") }
        positions.map{
            var position = it
            var steps = 0
            while (!position.endsWith("Z")) {
                val leftOrRight = seq[steps % seq.size]
                steps++
                position = if (leftOrRight == 'L') routeMap[position]!!.first else routeMap[position]!!.second
            }
            steps
        }
        .fold(1L) { acc, i -> lcm(acc, i.toLong()) }.printAnswer()
    }

    private fun lcm(a:Long, b:Long): Long {
        val larger = if (a > b) a else b
        var lcm = larger
        while (!(lcm % a == 0L && lcm % b == 0L)) {
            lcm += larger
        }
        return lcm
    }
}