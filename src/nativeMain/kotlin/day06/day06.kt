package day06

import fold
import solveDay

fun main07() {
    // Part 1
    solveDay(6, { TEST_INPUT_DAY_6 }) { findPacketEndPosition(it, 4) }
    // Part 2
    solveDay(6, { TEST_INPUT_DAY_6 }) { findPacketEndPosition(it, 14) }
}

const val TEST_INPUT_DAY_6 = """nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"""

fun findPacketEndPosition(input: String, length: Int): Int =
    input.windowed(length)
        .zip(generateSequence(1) { it }.take(input.length).toList())
        .dropWhile { !allCharactersUnique(it.first) }
        .take(1)
        .first()
        .fold { s, pos -> s.length + pos }

fun allCharactersUnique(s: String): Boolean = s.toCharArray().toSet().size == s.length