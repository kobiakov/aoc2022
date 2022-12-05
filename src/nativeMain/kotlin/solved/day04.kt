package solved

import fold
import map
import solveDay

fun main04() {
    // Part 1
    solveDay(4, { TEST_INPUT_DAY_4 }) { input ->
        input.trim().lineSequence()
            .map(::toPairOfSectionRanges)
            .filter(::oneIsFullyContained)
            .count()
    }
    // Part 2
    solveDay(4, { TEST_INPUT_DAY_4 }) { input ->
        input.trim().lineSequence()
            .map(::toPairOfSectionRanges)
            .filter(::haveOverlap)
            .count()
    }
}

val TEST_INPUT_DAY_4 = """
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8
""".trim()

typealias SectionRange = UIntRange

fun toPairOfSectionRanges(s: String): Pair<SectionRange, SectionRange> =
    s.split(",").zipWithNext().first().map(::toSectionRange)

fun toSectionRange(s: String): SectionRange =
    s.split("-").zipWithNext().first().fold { l, r -> SectionRange(l.toUInt(), r.toUInt()) }

fun oneIsFullyContained(pair: Pair<SectionRange, SectionRange>): Boolean = with(pair) {
    first.contains(second.first) && first.contains(second.last) || second.contains(first.first) && second.contains(first.last)
}

fun haveOverlap(pair: Pair<SectionRange, SectionRange>): Boolean = with(pair) {
    first.contains(second.first) || first.contains(second.last) || second.contains(first.first) || second.contains(first.last)
}