fun main() {
    // Part 1
    solveDay(3, { TEST_INPUT_DAY_3 }) { input ->
        input.trim().lines()
            .map(::stringToRucksack)
            .map(::totalPriorityForRucksack)
            .sum()
    }
    // Part 2
    solveDay(3, { TEST_INPUT_DAY_3 }) { input ->
        input.trim().lines()
            .map(::stringToRucksack)
            .chunked(3)
            .map(List<Rucksack>::findBadge)
            .map(::itemPriority)
            .sum()
    }
}

val TEST_INPUT_DAY_3 = """
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw    
""".trim()

data class Rucksack(val first: Compartment, val second: Compartment)

typealias Compartment = Set<Item>
typealias Item = Char

fun splitStringInHalf(s: String): Pair<String, String> = s.chunked(s.length / 2).zipWithNext().first()

fun makeCompartments(pairOfStrings: Pair<String, String>): Pair<Compartment, Compartment> =
    pairOfStrings.map(::stringToCompartment)

fun stringToCompartment(s: String): Compartment = s.toSet()

fun stringToRucksack(s: String): Rucksack = makeCompartments(splitStringInHalf(s)).fold(::Rucksack)

fun itemsInBothCompartments(rucksack: Rucksack): Set<Item> = rucksack.first.intersect(rucksack.second)

typealias Priority = Int

fun itemPriority(item: Item): Priority = if (item >= 'a') item - 'a' + 1 else item - 'A' + 27

fun totalPriorityForRucksack(rucksack: Rucksack): Priority =
    itemsInBothCompartments(rucksack).map(::itemPriority).sum()

fun List<Rucksack>.findBadge(): Item =
    zipWithNext { l, r -> (l.first + l.second).intersect(r.first + r.second) }
        .reduce { l, r -> l.intersect(r) }
        .only()