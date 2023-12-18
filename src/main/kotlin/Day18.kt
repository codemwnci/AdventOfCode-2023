import java.io.File

fun main() {
    Day18().puzzle1()
    Day18().puzzle2()
}

class Day18 {
    private val file = File("inputs/day18.txt")
    data class Point(val row:Int, val col:Int) {
        fun move(dir: Dir, distance:Int = 1) = when(dir) {
            Dir.N -> Point(this.row-distance, this.col)
            Dir.S -> Point(this.row+distance, this.col)
            Dir.E -> Point(this.row, this.col+distance)
            Dir.W -> Point(this.row, this.col-distance)
        }
        fun inBounds(grid: List<List<Any>>) = this.row in grid.indices && this.col in grid[0].indices
    }
    enum class Dir { N, S, E, W }

    data class Instruction(val dir:Dir, val dist:Int)

    // Point class from previous days made it very quick to create the List<Point> for the points in the polygon
    // Took way too long trying to implement Shoelace formula + Pick's Theorem (thanks Reddit).
    fun puzzle1() {
        val instructions = file.readLines().map { line ->
            line.split(" ").let {
                Instruction(
                    when(it[0]) {
                        "R" -> Dir.E
                        "L" -> Dir.W
                        "D" -> Dir.S
                        else -> Dir.N
                    },
                    it[1].toInt()
                )
            }
        }

        var perim = 0L
        val points = instructions.fold(mutableListOf(Point(0,0))) { acc, instruction ->
            acc.add(acc.last().move(instruction.dir, instruction.dist))
            perim += instruction.dist
            acc
        }

        val area = points.zipWithNext { a, b -> (a.row + b.row).toLong() * (a.col - b.col).toLong() }.sum() / 2
        (area + perim / 2 + 1).printAnswer()
    }

    // Part 2 was almost identical to part 1, just a slightly different selector on the Point constructor
    // and making sure the Area didn't Integer overflow.
    @OptIn(ExperimentalStdlibApi::class)
    fun puzzle2() {
        val instructions = file.readLines().map { line ->
            val hex = line.substringAfter("(#").take(6)
            Instruction(
                when(hex.takeLast(1)) {
                    "0" -> Dir.E
                    "1" -> Dir.S
                    "2" -> Dir.W
                    else -> Dir.N
                },
                hex.take(5).hexToInt()
            )
        }

        var perim = 0L
        val points = instructions.fold(mutableListOf(Point(0,0))) { acc, instruction ->
            acc.add(acc.last().move(instruction.dir, instruction.dist))
            perim += instruction.dist
            acc
        }

        val area = points.zipWithNext { a, b -> (a.row + b.row).toLong() * (a.col - b.col).toLong() }.sum() / 2
        (perim / 2 + area + 1).printAnswer()
    }
}