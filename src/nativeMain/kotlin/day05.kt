fun main() {
    // Part 1
    solveDay(5, { TEST_INPUT_DAY_5 }) { input ->
        separateStacksFromCommands(input.lineSequence())
            .map(left = ::parseStacks, right = ::parseCommands)
            .fold(::initiateCrateMover9000)
            .tip()
            .joinToString("")
    }
    // Part 2
    solveDay(5, { TEST_INPUT_DAY_5 }) { input ->
        separateStacksFromCommands(input.lineSequence())
            .map(left = ::parseStacks, right = ::parseCommands)
            .fold(::initiateCrateMover9001)
            .tip()
            .joinToString("")
    }
}

const val TEST_INPUT_DAY_5 = """    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2"""

fun separateStacksFromCommands(input: Sequence<String>): Pair<Sequence<String>, Sequence<String>> =
    input.split(String::isBlank)

fun parseStacks(input: Sequence<String>): CrateStacks = layersToStack(input.takeWhile(::isLayer).map(::parseLayer))
fun parseLayer(s: String): List<CrateLabel> = if (s.length < 3) listOf() else listOf(s[1]) + parseLayer(s.drop(4))
fun isLayer(s: String): Boolean = s.trimStart().startsWith("[")
fun layersToStack(layers: Sequence<List<CrateLabel>>): CrateStacks =
    layers.fold(emptyStacks(determineNumberOfStacksFromLayers(layers))) { stacks, layer ->
        stacks.zip(layer).map { it.fold { stack, label -> if (label == ' ') stack else stack + label } }
    }
fun determineNumberOfStacksFromLayers(layers: Sequence<List<CrateLabel>>) = layers.first().size
fun emptyStacks(numberOfStacks: Int): CrateStacks = generateSequence { listOf<CrateLabel>() }.take(numberOfStacks).toList()

fun parseCommands(input: Sequence<String>): Commands =
    input.takeWhile { it.startsWith("move") }.map(::parseCommand).toList()
fun parseCommand(s: String) =
    with(s.split(" ")) { Command(this[1].toInt(), this[3].toInt() - 1, this[5].toInt() - 1) }

typealias CrateLabel = Char
typealias CrateStack = List<CrateLabel>
typealias CrateStacks = List<CrateStack>
data class Command(val numberOfCrates: Int, val stackFrom: Int, val stackTo: Int)
typealias Commands = List<Command>

fun Command.minusCrate(): Command = with (this) { Command(numberOfCrates - 1, stackFrom, stackTo) }
fun Command.executeAsCM9000(stacks: CrateStacks): CrateStacks =
    if (numberOfCrates <= 0) stacks else minusCrate().executeAsCM9000(stacks.moveOne(stackFrom, stackTo))
fun Command.executeAsCM9001(stacks: CrateStacks): CrateStacks = stacks.moveManyAtOnce(numberOfCrates, stackFrom, stackTo)

fun CrateStacks.moveOne(stackFrom: Int, stackTo: Int): CrateStacks = moveManyAtOnce(1, stackFrom, stackTo)
fun CrateStacks.moveManyAtOnce(numberOfCrates: Int, stackFrom: Int, stackTo: Int) = this
    .withItemAt(stackFrom, this[stackFrom].drop(numberOfCrates))
    .withItemAt(stackTo, this[stackFrom].take(numberOfCrates) + this[stackTo])

fun CrateStacks.tip(): List<CrateLabel> = this.map(List<CrateLabel>::first).toList()

fun initiateCrateMover9000(stacks: CrateStacks, commands: Commands): CrateStacks =
    commands.fold(stacks) { s, command -> command.executeAsCM9000(s) }

fun initiateCrateMover9001(stacks: CrateStacks, commands: Commands): CrateStacks =
    commands.fold(stacks) { s, command -> command.executeAsCM9001(s) }
