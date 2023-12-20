import java.io.File

fun main() {
    Day20().puzzle1()
    Day20().puzzle2()
}

class Day20 {
    private val file = File("inputs/day20.txt")
    enum class Type { Flip, Conj, Broad, Untyped }
    data class Module(private val nameRaw:String, val dest: List<String>, var state: Boolean = false, val memory: MutableMap<String, Int> = mutableMapOf()) {
        val type: Type
            get() = when(nameRaw.first()) {
                'b' -> Type.Broad
                '%' -> Type.Flip
                else -> Type.Conj
            }

        val name: String
            get() = when(type) {
                Type.Broad -> nameRaw
                else -> nameRaw.drop(1)
            }
    }

    private fun lookupModule(name: String) = modules[name] ?: Module(name, emptyList())

    private val modules = file.readLines().map {
        it.split(" -> ").let {
            Module(it[0], it[1].split(", "))
        }
    }.associateBy { it.name }



    fun puzzle1() { solve(false) }
    fun puzzle2() { solve(true) }

    private fun solve(part2: Boolean) {
        data class ToProcess(val module: Module, val pulse: Int, val sender: String)
        var low = 0
        var high = 0
        val cycleLengths = mutableMapOf<String, Int>()
        val numCycles = modules.values.count { it.dest.contains("lg") }

        repeat(if (part2) Int.MAX_VALUE else 1000) { buttonPresses ->
            val queue = ArrayDeque<ToProcess>()
            queue.add(ToProcess(modules["broadcaster"]!!, 0, "button"))

            while (queue.isNotEmpty()) {
                val (module, pulse, sender) = queue.removeFirst()
                if (pulse == 0) low++ else high++

                when (module.type) {
                    Type.Broad -> module.dest.forEach { queue.add(ToProcess(lookupModule(it), pulse, module.name)) }
                    Type.Flip -> {
                        if (pulse == 0) { // if input low pulse, then flip state
                            module.state = !module.state
                            module.dest.forEach { queue.add(ToProcess(lookupModule(it), if (module.state) 1 else 0, module.name)) }
                        }
                    }
                    Type.Conj -> {
                        module.memory[sender] = pulse
                        modules.values.filter { it.dest.contains(module.name) }.let { conjInputs ->
                            val out = if (conjInputs.all { (module.memory[it.name] ?: 0) == 1 }) 0 else 1
                            module.dest.forEach { queue.add(ToProcess(lookupModule(it), out, module.name)) }
                        }

                        // In my input, as with others, rx is fed by a single Conjunction (lg), which is fed by 4 other Conjunctions.
                        // Need to find the LCM of each sub-graph's (the 4 conjunctions) cycle length to calculate when
                        // rx will output
                        if (part2 && module.name=="lg" && pulse == 1 && cycleLengths[sender] == null) {
                            cycleLengths[sender] = buttonPresses+1
                            if (cycleLengths.size == numCycles) {
                                lcm(cycleLengths.values).printAnswer()
                                return
                            }
                        }
                    }
                    Type.Untyped -> {  } // do nothing
                }
            }
        }

        // should only get here in part 1
        println("High: $high - low: $low")
        (high.toLong() * low).printAnswer()
    }
}