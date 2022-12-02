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
