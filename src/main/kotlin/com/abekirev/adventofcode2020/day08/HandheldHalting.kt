package com.abekirev.adventofcode2020.day08

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day08", "input.txt").useLinesFromResource { lines ->
            Computer(
                Program(
                    lines
                        .instructions()
                        .toList()
                )
            )
                .runUntilInstructionExecutionRepeats()
                ?.accumulator
                ?.value
        }
    )

private fun Computer.runUntilInstructionExecutionRepeats(): State? {
    val executedLines = mutableSetOf<Int>()
    var state: State? = null
    do {
        if (state != null)
            executedLines += state.pointer.value
        state = step()
    } while (state?.pointer?.value !in executedLines)
    return state
}

private fun Sequence<String>.instructions() =
    map(String::toInstruction)

private fun String.toInstruction(): Instruction {
    val (type, parameterStr) = split(" ")
    val parameter = parameterStr.toInt()
    return when (type) {
        "acc" -> AccInstruction(parameter)
        "jmp" -> JumpInstruction(parameter)
        "nop" -> NopInstruction(parameter)
        else -> throw IllegalArgumentException("Unknown instruction: $this")
    }
}

private class Computer private constructor(
    private val program: Program,
    private var state: State,
) {
    constructor(program: Program) : this(program, State())

    fun step(): State? =
        when (val instruction = program[state.pointer.value]) {
            is Instruction -> {
                state = instruction.execute(state)
                state
            }
            else -> null
        }
}

private class Program(
    private val instructions: List<Instruction>
) {
    operator fun get(index: Int): Instruction? =
        try {
            instructions[index]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
}

private data class State(
    val pointer: Pointer = Pointer(0),
    val accumulator: Accumulator = Accumulator(0)
)

private interface Instruction {
    fun execute(state: State): State
}

private class AccInstruction(
    private val delta: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        accumulator = state.accumulator.changeBy(delta),
        pointer = state.pointer.changeBy(1)
    )
}

private class NopInstruction(
    private val delta: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        pointer = state.pointer.changeBy(1)
    )
}

private class JumpInstruction(
    private val delta: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        pointer = state.pointer.changeBy(delta)
    )
}

private data class Accumulator(
    val value: Int
) {
    fun changeBy(delta: Int) = Accumulator(value + delta)
}

private data class Pointer(
    val value: Int
) {
    fun changeBy(delta: Int) = Pointer(value + delta)
}