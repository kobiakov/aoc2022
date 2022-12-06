fun main() {
    // Part 1
    solveDay(6, { TEST_INPUT_DAY_6 }) { it.findPacketEndPosition(4) }
    // Part 2
    solveDay(6, { TEST_INPUT_DAY_6 }) { it.findPacketEndPosition(14) }
}

const val TEST_INPUT_DAY_6 = """nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"""

fun String.findPacketEndPosition(length: Int): Int =
    this.windowed(length)
        .zip(generateSequence(1) { it }.take(this.length).toList())
        .dropWhile { !allCharactersUnique(it.first) }
        .take(1)
        .first()
        .fold { s, pos -> s.length + pos }

fun allCharactersUnique(s: String): Boolean = s.toCharArray().toSet().size == s.length