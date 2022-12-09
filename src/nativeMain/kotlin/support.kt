import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

suspend fun fetchInput(day: Int): String {
    val client = HttpClient(Curl)
    val response: HttpResponse = client.get {
        url("https://adventofcode.com/2022/day/$day/input")
        header(
            "cookie",
            // NOTE: you'll need this token for the app to compile.
            // Just create a file, say, token.kt, and make a const there.
            "session=${AOC_TOKEN}"
        )
    }
    if (response.status != HttpStatusCode.OK) {
        throw RuntimeException("status != 200: $response")
    }
    return response.bodyAsText()
}

typealias inputFunction = suspend (day: Int) -> String

fun solveDay(dayNr: Int, input: inputFunction = ::fetchInput, solver: (input: String) -> Any?) {
    runBlocking {
        println("The answer is ${solver(input(dayNr))}")
    }
}

fun <T> List<T>.partitionBy(predicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf(mutableListOf<T>())) { acc, t ->
        if (predicate(t)) {
            acc.add(mutableListOf())
        } else {
            acc.last().add(t)
        }
        acc
    }.filterNot { it.isEmpty() }

fun <I, O> Pair<I, I>.map(mapping: (I) -> O): Pair<O, O> = Pair(mapping(first), mapping(second))
fun <I, L, R> Pair<I, I>.map(left: (I) -> L, right: (I) -> R): Pair<L, R> = Pair(left(first), right(second))

fun <L, R, O> Pair<L, R>.fold(folder: (L, R) -> O): O = folder(first, second)

fun <T> Collection<T>.only() =
    if (size != 1) throw IllegalStateException("the collection is expected to only have one element: $this") else first()

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Pair<Sequence<T>, Sequence<T>> =
    Pair(this.takeWhile { !predicate(it) }, this.dropWhile { !predicate(it) }.drop(1))

fun <T> List<T>.withItemAt(index: Int, value: T) =
    this.take(index) + value + this.drop(index + 1)

fun <K, V> Map<K, V>.putUnlessContainsKey(key: K, value: V) =
    if (containsKey(key)) this else this + (key to value)

typealias Matrix<T> = List<List<T>>
fun <T> Matrix<T>.transpose(times: Int): Matrix<T> = (1..times).fold(this) { acc, _ -> acc.transpose() }
fun <T> Matrix<T>.transpose(): Matrix<T> = (0 until first().size)
    .flatMap { column -> (this.size - 1 downTo 0).map { row -> this[row][column] } }
    .chunked(size)
    .toList()