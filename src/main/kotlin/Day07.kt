import java.io.File

fun main() {
    Day07().puzzle1()
    Day07().puzzle2()
}

class Day07 {
    private val file = File("inputs/day07.txt")

    data class Hand(val cards: List<Char>, val bid:Int, val wildcardRules: Boolean = false): Comparable<Hand> {
        private fun wildCardHand(): Hand {
            val bestWildcard = "23456789TQKA".maxBy { possWildcard ->
                val newHand = cards.map { if (it == 'J') possWildcard else it }
                Hand(newHand, bid, wildcardRules).rank1Score()
            }
            return Hand(cards.map { if (it == 'J') bestWildcard else it }, bid, wildcardRules)
        }

        override fun compareTo(other: Hand): Int {
            val thisScore = if (wildcardRules) wildCardHand().rank1Score() else rank1Score()
            val otherScore = if (wildcardRules) other.wildCardHand().rank1Score() else other.rank1Score()

            if (thisScore > otherScore) return 1
            else if (thisScore < otherScore) return -1

            // if we get here, compare first cards score
            cards.forEachIndexed { index, c ->
                if (cardValue(c) > cardValue(other.cards[index])) return 1
                if (cardValue(c) < cardValue(other.cards[index])) return -1
            }

            return 0 // should never get here, unless both hands are equal
        }

        private fun rank1Score(): Int {
            val fives = cards.combinations(5).count { combination -> combination.all { it == combination[0] } }
            val fours = cards.combinations(4).count { combination -> combination.all { it == combination[0] } }
            val threes =cards.combinations(3).count { combination -> combination.all { it == combination[0] } }
            val pairs = cards.combinations(2).count { combination -> combination.all { it == combination[0] } }

            return fives*10000 + fours*1000 + threes*100 + pairs*10
        }

        private fun cardValue(card: Char): Int = when(card) {
            'A' -> 14
            'K' -> 13
            'Q' -> 12
            'J' -> if (wildcardRules) 1 else 11
            'T' -> 10
            else -> card.digitToInt()
        }
    }

    fun puzzle1() {
        val hands = file.readLines().map {  it.split(" ").let {Hand(it[0].toList(), it[1].toInt(), false) } }
        hands.sorted().mapIndexed { index, hand -> hand.bid * (index+1) }.sum().printAnswer()
    }

    fun puzzle2() {
        val hands = file.readLines().map {  it.split(" ").let {Hand(it[0].toList(), it[1].toInt(), true) } }
        hands.sorted().mapIndexed { index, hand -> hand.bid * (index+1) }.sum().printAnswer()
    }
}