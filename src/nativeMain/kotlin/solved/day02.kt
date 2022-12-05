package solved

import solveDay
import solved.Outcome.*
import solved.Shape.*

fun main02() {
    // Part 1
    solveDay(2, { TEST_INPUT_DAY_2 }) { input ->
        inputToRounds(input)
            .map(::scoreForRound)
            .sum()
    }
    // Part 2
    solveDay(2, { TEST_INPUT_DAY_2 }) { input ->
        inputToUnfairRounds(input)
            .map(::scoreForRound)
            .sum()
    }
}

const val TEST_INPUT_DAY_2 = """
A Y
B X
C Z    
"""

sealed interface Shape {
    object Rock : Shape
    object Paper : Shape
    object Scissors : Shape
}

sealed interface Outcome {
    object Draw : Outcome
    object Win : Outcome
    object Loss : Outcome
}

data class Round(val opponent: Shape, val player: Shape)

infix fun Shape.beats(other: Shape): Boolean = when (this) {
    Rock -> other is Scissors
    Paper -> other is Rock
    Scissors -> other is Paper
}

fun Shape.beatenBy(): Shape = when (this) {
    Paper -> Scissors
    Rock -> Paper
    Scissors -> Rock
}

fun Shape.beats(): Shape = when (this) {
    Paper -> Rock
    Rock -> Scissors
    Scissors -> Paper
}

fun outcomeForPlayer(round: Round): Outcome = with(round) {
    when {
        player beats opponent -> Win
        opponent beats player -> Loss
        else -> Draw
    }
}

typealias Score = Int

fun scoreForShape(shape: Shape): Score = when (shape) {
    Rock -> 1
    Paper -> 2
    Scissors -> 3
}

fun scoreForOutcome(outcome: Outcome): Score = when (outcome) {
    Loss -> 0
    Draw -> 3
    Win -> 6
}

fun scoreForRound(round: Round): Score = with(round) {
    scoreForOutcome(outcomeForPlayer(round)) + scoreForShape(player)
}

fun inputToRounds(input: String): Sequence<Round> =
    input.lineSequence()
        .map(String::trim)
        .filterNot(String::isBlank)
        .map(::parseRound)

fun parseRound(roundLine: String): Round =
    roundLine.split(" ")
        .chunked(2)
        .map { (left, right) -> Round(opponent = parseOpponentShape(left), player = parsePlayerShape(right)) }
        .first()

fun parseOpponentShape(s: String): Shape = when(s) {
    "A" -> Rock
    "B" -> Paper
    "C" -> Scissors
    else -> throw IllegalArgumentException("opponent shape: don't know what to do with '$s'")
}

fun parsePlayerShape(s: String): Shape = when(s) {
    "X" -> Rock
    "Y" -> Paper
    "Z" -> Scissors
    else -> throw IllegalArgumentException("player shape: don't know what to do with '$s'")
}

// Part 2

fun parseDesiredOutcome(s: String): Outcome = when(s) {
    "X" -> Loss
    "Y" -> Draw
    "Z" -> Win
    else -> throw IllegalArgumentException("player strategy: don't know what to do with '$s'")
}

fun shapeForDesiredOutcome(desiredOutcome: Outcome, opponent: Shape): Shape = when(desiredOutcome) {
    Draw -> opponent
    Loss -> opponent.beats()
    Win -> opponent.beatenBy()
}

fun inputToUnfairRounds(input: String): Sequence<Round> =
    input.lineSequence()
        .map(String::trim)
        .filterNot(String::isBlank)
        .map(::parseUnfairRound)

fun parseUnfairRound(roundLine: String): Round =
    roundLine.split(" ")
        .chunked(2)
        .map { (left, right) ->
            Round(
                opponent = parseOpponentShape(left),
                player = shapeForDesiredOutcome(parseDesiredOutcome(right), parseOpponentShape(left))
            ) }
        .first()