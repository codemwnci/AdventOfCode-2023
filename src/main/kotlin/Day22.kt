import kotlinx.coroutines.*
import java.io.File

fun main() {
    Day22().puzzle1()
    Day22().puzzle2()
}

class Day22 {
    private val file = File("inputs/day22.txt")
    data class Brick(val x:IntRange, val y:IntRange, var z:IntRange)

    fun puzzle1() {
        val bricks = file.readLines().map{ it ->
            it.split("~").map { it.split(",").map { it.toInt() } }.let {
                Brick(it[0][0]..it[1][0], it[0][1]..it[1][1], it[0][2]..it[1][2])
            }
        }.sortedBy { it.z.last }

        bricks.forEachIndexed { index, brick ->
            val below = bricks.subList(0, index)
            val height = brick.z.last - brick.z.first
            val fallTo = below.filter {
                (brick.x intersect it.x).isNotEmpty() && (brick.y intersect it.y).isNotEmpty()
            }
            val fallToHeight = fallTo.maxOfOrNull { it.z.last } ?: 0
            brick.z = (fallToHeight + 1) .. (fallToHeight + 1 + height)
        }

        val cantRemove = bricks.count { brick ->
            val simRemove = bricks.filter { brick != it } // simulate removing a brick
            simRemove.any { b ->
                simRemove.none { b.z.first == 1 || ((it.x intersect b.x).isNotEmpty() && (it.y intersect b.y).isNotEmpty() && b.z.first == it.z.last + 1) }
            }
        }

        (bricks.size - cantRemove).printAnswer()
    }

    fun puzzle2() {
        val bricks = file.readLines().map{ it ->
            it.split("~").map { it.split(",").map { it.toInt() } }.let {
                Brick(it[0][0]..it[1][0], it[0][1]..it[1][1], it[0][2]..it[1][2])
            }
        }.sortedBy { it.z.last }

        bricks.forEachIndexed { index, brick ->
            val below = bricks.subList(0, index)
            val height = brick.z.last - brick.z.first
            val fallTo = below.filter {
                (brick.x intersect it.x).isNotEmpty() && (brick.y intersect it.y).isNotEmpty()
            }
            val fallToHeight = fallTo.maxOfOrNull { it.z.last } ?: 0
            brick.z = (fallToHeight + 1) .. (fallToHeight + 1 + height)
        }

        // Based on part 1 not being particularly quick to process, decided to use coroutines to run the checks in
        // parallel. I went back later and checked the time taken if this was done serially, and it takes >5x
        // This approach takes ~2m48s
        runBlocking {
            bricks.map { brick ->
                async(Dispatchers.Default) { countFallers(bricks.filter { brick != it }) }
            }.awaitAll().sum().printAnswer()
        }

        // This approach takes ~13m8s
        //bricks.sumOf { brick -> countFallers(bricks.filter { brick != it }) }.printAnswer()
    }

    private fun countFallers(bricks: List<Brick>): Int {
        // count how many bricks would fall in this list
        val fallers = bricks.filter { b ->
            bricks.none { b.z.first == 1 || ((it.x intersect b.x).isNotEmpty() && (it.y intersect b.y).isNotEmpty() && b.z.first == it.z.last + 1) }
        }

        // if zero, return zero (and potentially end the recursion
        // if non-zero, count the fallers, and reprocess this list minus the fallers
        // (don't need to track how far they fall, just that they have fallen, hence why removal works)
        return if (fallers.isEmpty()) 0
        else fallers.size + countFallers(bricks.filter { !fallers.contains(it) })
    }
}