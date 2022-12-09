package day08

import solveDay

fun main08() {
    // Part 1
    solveDay(8, { TEST_INPUT_DAY_8 }) { input ->
        inputToForest(input).findTrees { forest, coordinate -> forest.isTreeVisibleFromOutside(coordinate) }.count()
    }
    // Part 2
    solveDay(8) { input ->
        inputToForest(input).mapTrees { forest, coordinate -> forest.scenicScore(coordinate) }.max()
    }
}

val TEST_INPUT_DAY_8 = """
30373
25512
65332
33549
35390
""".trim()

typealias Height = Char
typealias Forest = List<List<Height>>

data class Coordinate(val row: Int, val column: Int)
data class Direction(val dRow: Int, val dColumn: Int) {
    companion object {
        fun all(): List<Direction> = listOf(
            Direction(-1, 0),
            Direction(0, 1),
            Direction(1, 0),
            Direction(0, -1)
        )
    }
}
typealias ScenicScore = Int
typealias ViewingDistance = Int

operator fun Coordinate.plus(direction: Direction): Coordinate =
    Coordinate(row + direction.dRow, column + direction.dColumn)

fun Forest.heightAt(coordinate: Coordinate): Height = this[coordinate.row][coordinate.column]
fun Forest.heightsInDirection(startingPointExcluding: Coordinate, direction: Direction): List<Height> =
    allPointsInDirection(startingPointExcluding, direction).map(::heightAt)

fun Forest.containsCoordinate(coordinate: Coordinate): Boolean =
    coordinate.row >= 0 && coordinate.column >= 0 && coordinate.row < size && coordinate.column < first().size

fun Forest.allPointsInDirection(startingPointExcluding: Coordinate, direction: Direction): List<Coordinate> =
    generateSequence(startingPointExcluding) { p -> p + direction }
        .drop(1)
        .takeWhile(this::containsCoordinate)
        .toList()

fun Forest.isTreeVisibleFromOutside(coordinate: Coordinate) =
    Direction.all().any { direction -> isTreeVisibleFromOutsideDirection(coordinate, direction) }

fun Forest.isTreeVisibleFromOutsideDirection(coordinate: Coordinate, direction: Direction) =
    heightsInDirection(coordinate, direction).all { it < heightAt(coordinate) }

fun Forest.scenicScore(coordinate: Coordinate): ScenicScore = Direction.all()
    .map { direction -> viewingDistance(coordinate, direction) }
    .fold(1) { acc, d -> acc * d }

fun Forest.viewingDistance(coordinate: Coordinate, direction: Direction): ViewingDistance =
    heightsInDirection(coordinate, direction).takeWhile { it < heightAt(coordinate) }.count() +
            heightsInDirection(coordinate, direction).dropWhile { it < heightAt(coordinate) }.take(1).count()


fun Forest.allTrees(): List<Coordinate> =
    indices.flatMap { row -> (0 until first().size).map { column -> Coordinate(row, column) } }

fun Forest.findTrees(predicate: (Forest, Coordinate) -> Boolean): List<Coordinate> =
    allTrees().filter { predicate(this, it) }

fun <T> Forest.mapTrees(mapper: (Forest, Coordinate) -> T): List<T> = allTrees().map { mapper(this, it) }

fun inputToForest(input: String): Forest = input.trim().lines().map { it.toCharArray().toList() }

