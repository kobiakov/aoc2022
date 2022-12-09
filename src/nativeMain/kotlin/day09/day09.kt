package day09

import solveDay
import withItemAt
import kotlin.math.abs

fun main09() {
    // Part 1
    solveDay(9, { TEST_INPUT_DAY_9 }) { input ->
        doRopeStuff(ropeLength = 2, parseInput(input)).visits.size
    }
    // Part 2
    solveDay(9, { TEST_INPUT_DAY_9_PART_2 }) { input ->
        doRopeStuff(ropeLength = 10, parseInput(input)).visits.size
    }
}

val TEST_INPUT_DAY_9 = """
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
""".trim()

val TEST_INPUT_DAY_9_PART_2 = """
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20    
""".trim()

data class Command(val direction: Char, val times: Int)

data class Coordinate(val row: Int, val column: Int) {
    fun isAdjacentWith(other: Coordinate): Boolean =
        abs(row - other.row) <= 1 && abs(column - other.column) <= 1

    fun directionToCloseDistance(other: Coordinate): Direction =
        Direction(approach(row, other.row), approach(column, other.column))

    private fun approach(from: Int, to: Int): Int = when {
        from > to -> -1
        from < to -> 1
        else -> 0
    }

    override fun toString(): String = "[$row, $column]"

    companion object {
        fun start(): Coordinate = Coordinate(0, 0)
    }
}

operator fun Coordinate.plus(direction: Direction): Coordinate =
    Coordinate(row + direction.dRow, column + direction.dColumn)


data class Direction(val dRow: Int, val dColumn: Int) {
    companion object {
        fun fromRawDirection(rawDirection: Char): Direction = when (rawDirection) {
            'U' -> Direction(-1, 0)
            'D' -> Direction(1, 0)
            'L' -> Direction(0, -1)
            'R' -> Direction(0, 1)
            else -> none()
        }

        fun none(): Direction = Direction(0, 0)
    }
}

typealias Visits = Set<Coordinate>
typealias Rope = List<Coordinate>

data class State(
    val ropeHeadToTail: Rope,
    val visits: Visits = setOf(Coordinate.start())
) {
    fun apply(command: Command): State = (1..command.times)
        .map { Direction.fromRawDirection(command.direction) }
        .fold(this) { acc, direction -> acc.moveHead(direction).moveOthers().noteTailVisit() }

    fun moveHead(direction: Direction) =
        copy(ropeHeadToTail = ropeHeadToTail.withItemAt(0, ropeHeadToTail[0] + direction))

    fun moveOthers(): State = copy(
        ropeHeadToTail = ropeHeadToTail.indices.drop(1).fold(ropeHeadToTail, ::closeDistanceIfNecessary)
    )

    fun closeDistanceIfNecessary(rope: Rope, knotNr: Int): Rope = with(rope[knotNr]) {
        if (this.isAdjacentWith(rope[knotNr - 1])) rope else
            rope.withItemAt(knotNr, this + this.directionToCloseDistance(rope[knotNr - 1]))
    }

    fun noteTailVisit(): State = copy(visits = visits + ropeHeadToTail.last())

    companion object {
        fun forRopeLength(length: Int) = State(ropeHeadToTail = (1..length).map { Coordinate.start() }.toList())
    }
}

fun parseInput(input: String): Sequence<Command> =
    input.trim().lineSequence().map { with(it.split(" ")) { Command(this[0][0], this[1].toInt()) } }

fun doRopeStuff(ropeLength: Int, commands: Sequence<Command>): State =
    commands.fold(State.forRopeLength(ropeLength)) { acc, command -> acc.apply(command) }