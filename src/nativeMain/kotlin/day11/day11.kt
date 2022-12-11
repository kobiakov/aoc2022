package day11

import partitionBy
import solveDay
import trimmed
import trimmedWords

fun main11() {
    // Part 1
    solveDay(11, { TEST_INPUT_DAY_11 }) { input ->
        monkeyBusiness(rounds(number = 20, monkeys = parseMonkeys(worryDivider = 3, input)))
    }
    // Part 2
    solveDay(11, { TEST_INPUT_DAY_11 }) { input ->
        monkeyBusiness(rounds(number = 10_000, monkeys = parseMonkeys(worryDivider = 1, input)))

    }
}

val TEST_INPUT_DAY_11 = """
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
"""

typealias MonkeyNumber = Int
typealias WorryLevel = Long
typealias Items = List<WorryLevel>
typealias Operation = (WorryLevel) -> WorryLevel

data class Test(val divider: Long, val ifTrue: MonkeyNumber, val ifFalse: MonkeyNumber) {
    fun apply(item: WorryLevel): MonkeyNumber = if (item % divider == 0L) ifTrue else ifFalse
}

typealias Monkeys = List<Monkey>

fun Monkeys.replace(number: MonkeyNumber, replacer: (Monkey) -> Monkey): Monkeys =
    this.map { if (it.number == number) replacer(it) else it }

data class Monkey(
    val number: MonkeyNumber,
    val items: Items = listOf(),
    val operation: Operation,
    val test: Test,
    val worryDivider: Int,
    val inspectCount: Int = 0
) {
    fun catchItem(item: WorryLevel): Monkey = copy(items = items + item)
    fun throwItem(): Monkey = copy(items = items.drop(1))
    fun inspect(): Monkey = copy(inspectCount = inspectCount + 1)
    fun takeTurn(monkeys: Monkeys): Monkeys = items.map { operation(it) / worryDivider }
        .fold(monkeys) { acc, item ->
            acc.replace(this.number) { me -> me.inspect().throwItem() }
                .replace(findTargetMonkey(item)) { other -> other.catchItem(item % lcmOfWorryDividers(monkeys)) }
        }

    fun findTargetMonkey(item: WorryLevel): MonkeyNumber = test.apply(item)
    fun lcmOfWorryDividers(monkeys: Monkeys): Long = monkeys.map { it.test.divider }.fold(1) { acc, i -> acc * i }
}

fun monkeyBusiness(monkeys: Monkeys): Long =
    monkeys.map { it.inspectCount }.sortedDescending().take(2).fold(1) { acc, i -> acc * i }

fun round(monkeys: Monkeys): Monkeys =
    monkeys.sortedBy { it.number }.map { it.number }
        .fold(monkeys) { acc, number -> acc.find { it.number == number }?.takeTurn(acc) ?: acc }

fun rounds(number: Int, monkeys: Monkeys): Monkeys = (1..number).fold(monkeys) { acc, roundNr -> round(acc) }

fun parseMonkeys(worryDivider: Int, input: String): Monkeys =
    input.trim().lines().partitionBy(String::isBlank).map { parseMonkey(worryDivider, it) }

fun parseMonkey(worryDivider: Int, lines: List<String>): Monkey = Monkey(
    number = parseMonkeyNumber(lines[0]),
    items = parseItems(lines[1]),
    operation = parseOperation(lines[2]),
    test = parseTest(lines.takeLast(3)),
    worryDivider = worryDivider
)

fun parseMonkeyNumber(input: String): MonkeyNumber = input.trim().dropLast(1).split(" ")[1].toInt()
fun parseItems(input: String): List<WorryLevel> =
    input.trim().split(":")[1].split(",").trimmed().map(String::toLong)

fun parseOperation(input: String): Operation = with(input.trim().split("=")[1].trimmedWords()) {
    {
        parseOperator(this[1])(
            (if (this[0] == "old") it else this[0].toLong()),
            (if (this[2] == "old") it else this[2].toLong())
        )
    }
}

fun parseOperator(input: String): (WorryLevel, WorryLevel) -> WorryLevel = when (input) {
    "*" -> { l, r -> l * r }
    "+" -> { l, r -> l + r }
    else -> { _, _ -> 0L }
}

fun parseTest(input: List<String>): Test = Test(
    divider = input[0].trim().split(" ").last().trim().toLong(),
    ifTrue = input[1].trim().split(" ").last().trim().toInt(),
    ifFalse = input[2].trim().split(" ").last().trim().toInt()
)