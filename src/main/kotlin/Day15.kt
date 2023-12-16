import java.io.File

fun main() {
    Day15().puzzle1()
    Day15().puzzle2()
}

class Day15 {
    private val file = File("inputs/day15.txt")

    fun puzzle1() {
        file.readText().split(",").sumOf {
            it.fold(0L) { acc, c -> (((acc + c.code) * 17) % 256) }
        }.printAnswer()
    }

    fun puzzle2() {
        val boxes = List(256) { ArrayDeque<Pair<String, Int>>() }
        file.readText().split(",").forEach {
            val label = it.split("-","=").first()
            val hash = label.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

            if (it.endsWith("-")) {
                boxes[hash].removeIf { it.first == label }
            }
            else { // ends with = and number
                val focalLength = it.substringAfter('=').toInt()
                val index = boxes[hash].indexOfFirst { it.first == label }
                if (index >= 0) {
                    boxes[hash].removeAt(index)
                    boxes[hash].add(index, label to focalLength)
                }
                else {
                    boxes[hash].addLast(label to focalLength)
                }
            }
        }

        boxes.mapIndexed { boxIndex, box ->
            if (box.isEmpty()) 0
            else box.mapIndexed { slot, lens -> (boxIndex+1) * (slot+1) * lens.second }.sum()
        }.sum().printAnswer()
    }
}