import java.io.File

fun main() {
    Day05().puzzle1()
    Day05().puzzle2()
}

class Day05 {

    data class RatioRange(val source: LongRange, val target: LongRange, val step: Long)

    private val file = File("inputs/day05.txt")
    val lines = file.readLines().split { it.isBlank() }
    val seeds = lines[0][0].substringAfter(": ").split(" ").map { it.toLong() }
    val ratios = lines.drop(1).map {
        it.drop(1).map {
            val (target, source, rangeSize) = it.split(" ").map { it.toLong() }
            RatioRange(source until source+rangeSize, target until target+rangeSize, target-source)
        }
    }

    private fun convertSeedToLocation(seed: Long) = ratios.fold(seed) { numToConvert, ratioRanges ->
        (ratioRanges.firstOrNull { seedToSoil ->
            seedToSoil.source.contains(numToConvert)
        }?.step ?: 0) + numToConvert
    }

    fun puzzle1() {
        seeds.minOf { convertSeedToLocation(it) }.printAnswer()
    }

    // brute force, but still completes quickly enough
    fun puzzle2() {
        seeds.windowed(2, 2).map { (start, length) -> start until start+length }.minOf { seedRange ->
            seedRange.minOf { convertSeedToLocation(it) }
        }.printAnswer()
    }
}