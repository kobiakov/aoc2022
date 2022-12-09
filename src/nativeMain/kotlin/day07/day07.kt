package day07

import putUnlessContainsKey
import solveDay

fun main07() {
    // Part 1
    solveDay(7, { TEST_INPUT_DAY_7 }) { input ->
        parseInput(input).map { state ->
            state.fs.keys
                .map { state.fs.totalSize(it) }
                .filter { it <= 100000 }
                .sum()
        }
    }
    // Part 2
    solveDay(7, { TEST_INPUT_DAY_7 }) { input ->
        parseInput(input).map { state ->
            state.fs.keys
                .map { state.fs.totalSize(it) }
                .filter { it >= spaceToFreeUp(state.fs) }
                .minOf { it }
        }
    }
}

const val TOTAL_DISK_SPACE = 70000000
const val REQUIRED_SPACE = 30000000

val TEST_INPUT_DAY_7 = """
${'$'} day07.cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} day07.cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} day07.cd e
${'$'} ls
584 i
${'$'} day07.cd ..
${'$'} day07.cd ..
${'$'} day07.cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k    
""".trim()

data class State(val pwd: Path = rootPath(), val fs: FS = emptyFS()) {
    fun <T> map(mapper: (State) -> T): T = mapper(this)
}

typealias Size = Int

data class File(val size: Size)

typealias Path = List<String>

fun rootPath(): Path = listOf()
fun stringToPath(s: String): Path = s.split("/")
fun Path.cd(arg: String): Path = when (arg) {
    ".." -> this.dropLast(1)
    else -> this + stringToPath(arg)
}

fun Path.equalsOrSubpathOf(other: Path) =
    other.size <= this.size && other.zip(this).map { it.first == it.second }.all { it }

typealias Folder = List<File>

fun emptyFolder(): Folder = listOf()

typealias FS = Map<Path, Folder>

data class Command(val op: String, val args: List<String> = listOf())

fun emptyFS(): FS = mapOf<Path, List<File>>().withDefault { listOf() }

fun parseInput(input: String): State =
    input.trim().lineSequence().fold(State()) { state, line ->
        when {
            line.startsWith("$") -> invokeCommand(state, parseCommand(line))
            line.startsWith("dir ") -> noteDownFolder(state, state.pwd.cd(parseDir(line)))
            line.first().isDigit() -> noteDownFile(state, parseFile(line))
            else -> state
        }
    }

fun parseCommand(input: String): Command = with(input.split(" ").dropWhile { it == "$" }) {
    Command(first(), drop(1))
}

fun parseDir(input: String): String = input.split(" ").drop(1).first()
fun parseFile(input: String): File = with(input.split(" ")) { File(size = this[0].toInt()) }

fun invokeCommand(state: State, command: Command): State = when (command.op) {
    "ls" -> state
    "day07.cd" -> with(relativeToPwd(state, command.args.first())) {
        noteDownFolder(state.copy(pwd = this), this)
    }

    else -> state
}

fun relativeToPwd(state: State, folder: String): Path = state.pwd.cd(folder)
fun noteDownFolder(state: State, path: Path): State =
    state.copy(fs = state.fs.putUnlessContainsKey(path, emptyFolder()))

fun noteDownFile(state: State, file: File): State =
    state.copy(fs = state.fs + (state.pwd to (state.fs[state.pwd].orEmpty() + file)))

fun FS.findFolders(predicate: (Path) -> Boolean): List<Path> = this.keys.filter(predicate)
fun FS.totalSize(path: Path): Size =
    findFolders { p -> p.equalsOrSubpathOf(path) }.sumOf { this[it].orEmpty().map(File::size).sum() }

fun spaceToFreeUp(fs: FS) = maxOf(0, fs.totalSize(rootPath()) - (TOTAL_DISK_SPACE - REQUIRED_SPACE))