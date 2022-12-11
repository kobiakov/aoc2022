package day10

import solveDay
import withItemAt

fun main10() {
    // Part 1
    solveDay(10, { TEST_INPUT_DAY_10 }) { input ->
        runProgram(parseInput(input)).signalStrength
    }
    // Part 2
    solveDay(10, { TEST_INPUT_DAY_10 }) { input ->
        runProgram(parseInput(input)).crt.visualize(40)
    }
}

val TEST_INPUT_DAY_10 = """
addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop
""".trim()

typealias Cycle = Int
typealias Register = Int
typealias Memory = Register
typealias Step = (Memory) -> Memory

data class CPU(val cycleCounter: Cycle = 1, val memory: Memory = 1) {
    fun step(step: Step): CPU = copy(cycleCounter = cycleCounter + 1, memory = step(memory))
}

data class Instruction(val steps: List<(Memory) -> Memory>) {
    companion object {
        private fun new(vararg steps: (Memory) -> Memory): Instruction = Instruction(steps.asList())

        fun noop(): Instruction = new({ it })
        fun addx(arg: Int): Instruction = new({ it }, { it + arg })
    }
}

typealias SignalStrength = Int

data class CRT(val currentPixel: Int = 0, val pixels: List<Char>) {
    fun moveRay(): CRT = copy(currentPixel = if (currentPixel + 1 == pixels.size) 0 else currentPixel + 1)
    fun draw(char: Char): CRT = copy(pixels = pixels.withItemAt(currentPixel, char))

    fun visualize(width: Int): String =
        pixels.chunked(width).joinToString("\n") { it.joinToString("") }

    companion object {
        fun forResolution(width: Int, height: Int): CRT = CRT(pixels = (1..(width * height)).map { ' ' }.toList())
    }
}

data class State(
    val cpu: CPU = CPU(),
    val signalStrength: SignalStrength = 0,
    val crt: CRT = CRT.forResolution(width = 40, height = 6)
) {
    fun tickCPU(step: Step): State = copy(cpu = cpu.step(step))
    fun introspect(): State = recalculateStrength()

    fun recalculateStrength(): State =
        if (isCycleToIntrospect(cpu.cycleCounter)) copy(signalStrength = signalStrength + signalStrength(cpu)) else this

    fun drawPixel(): State = copy(crt = crt.draw(determinePixelToDraw(cpu)).moveRay())
    fun determinePixelToDraw(cpu: CPU): Char =
        if ((cpu.cycleCounter % 40 - 1) in ((cpu.memory - 1)..(cpu.memory + 1))) '#' else '.'
}

fun isCycleToIntrospect(cycle: Cycle): Boolean = cycle in (20..220 step 40)
fun signalStrength(cpu: CPU) = cpu.cycleCounter * cpu.memory

fun parseInput(input: String): Sequence<Instruction> = input.trim().lineSequence().map(::parseCommand)

fun parseCommand(line: String): Instruction = with(line.split(" ")) {
    when (this[0]) {
        "noop" -> Instruction.noop()
        "addx" -> Instruction.addx(this[1].toInt())
        else -> Instruction.noop()
    }
}

fun runProgram(instructions: Sequence<Instruction>): State =
    instructions.flatMap(Instruction::steps)
        .fold(State()) { state, step -> state.drawPixel().tickCPU(step).introspect() }