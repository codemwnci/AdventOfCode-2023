import java.io.File

fun main() {
    Day19().puzzle1()
    Day19().puzzle2()
}

class Day19 {

    private val file = File("inputs/day19.txt")

    interface Rule
    data class OpRule(val c: Char, val op:Char, val num:Int, val goto: String) : Rule
    data class GoToRule(val goto: String) : Rule
    data class Workflow(val name:String, val rules: List<Rule>)

    private val inp:List<List<String>> = file.readLines().split { it.isBlank() }

    private val ratings = inp[1].map {
        it.drop(1).dropLast(1).split(",").map {
            it.split("=").let { category -> category[0].first() to category[1].toInt() }
        }
    }
    private val workflows = inp[0].map { flow ->
        Workflow(flow.substringBefore("{"),
            flow.substringAfter("{").dropLast(1).split(",").map {
                if (it.contains(":")) {
                    it.split(":").let { (op, goto) ->
                        OpRule(op.first(), op.drop(1).first(), op.drop(2).toInt(), goto)
                    }
                }
                else {
                    GoToRule(it)
                }
            }
        )
    }.associateBy { it.name }

    fun puzzle1() {
        fun processWorkflow(rating: List<Pair<Char, Int>>, flowName: String): Boolean {
            if (flowName == "A") return true
            if (flowName == "R") return false

            val workflow = workflows[flowName]
            workflow?.rules?.forEach { rule ->
                if (rule is GoToRule) return processWorkflow(rating, rule.goto)
                else if (rule is OpRule) {
                    val ratingToCompare = rating.first { rule.c == it.first }.second
                    if (rule.op == '<' && ratingToCompare < rule.num) return processWorkflow(rating, rule.goto)
                    else if (rule.op == '>' && ratingToCompare > rule.num) return processWorkflow(rating, rule.goto)
                }
                // if we get here, no rule fired so process next rule in the list
            }

            return false
        }

        ratings.filter { processWorkflow(it, "in") }
            .sumOf { it.sumOf { it.second } }
            .printAnswer()
    }

    fun puzzle2() {

        val validRanges = mutableListOf<List<IntRange>>()
        fun processWorkflow(ranges: List<IntRange>, flowName: String) {
            if (flowName == "R") return
            if (flowName == "A") {
                validRanges += ranges
                return
            }

            var (x,m,a,s) = ranges

            val workflow = workflows[flowName]
            workflow?.rules?.forEach { rule ->
                if (rule is GoToRule) processWorkflow(listOf(x,m,a,s), rule.goto)
                else if (rule is OpRule) {
                    if (rule.op == '<') when (rule.c) {
                        'x' -> { processWorkflow(listOf(x.first until rule.num, m, a, s), rule.goto); x = rule.num .. x.last }
                        'm' -> { processWorkflow(listOf(x, m.first until rule.num, a, s), rule.goto); m = rule.num .. m.last }
                        'a' -> { processWorkflow(listOf(x, m, a.first until rule.num, s), rule.goto); a = rule.num .. a.last }
                        's' -> { processWorkflow(listOf(x, m, a, s.first until rule.num), rule.goto); s = rule.num .. s.last }
                    }
                    else if (rule.op == '>') when (rule.c) {
                        'x' -> { processWorkflow(listOf((rule.num+1)..x.last, m, a, s), rule.goto); x = x.first .. rule.num }
                        'm' -> { processWorkflow(listOf(x, (rule.num+1)..m.last, a, s), rule.goto); m = m.first .. rule.num }
                        'a' -> { processWorkflow(listOf(x, m, (rule.num+1)..a.last, s), rule.goto); a = a.first .. rule.num }
                        's' -> { processWorkflow(listOf(x, m, a, (rule.num+1)..s.last), rule.goto); s = s.first .. rule.num }
                    }
                }
            }
        }

        processWorkflow(List(4) { 1..4000 }, "in")
        validRanges.sumOf { it.fold(1L) { acc, range -> (range.last - range.first + 1) * acc } }.printAnswer()
    }
}