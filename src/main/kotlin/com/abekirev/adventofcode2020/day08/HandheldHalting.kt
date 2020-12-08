package com.abekirev.adventofcode2020.day08

import com.abekirev.adventofcode2020.util.useLinesFromResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        Path.of("input", "day08", "input.txt").useLinesFromResource { lines ->
            Computer(
                Program(
                    lines
                        .instructions()
                        .toMap()
                )
            )
                .runUntilInstructionExecutionRepeats()
                ?.accumulator
                ?.value
        }
    )

private const val TIMEOUT_MILLIS: Long = 10

private fun partTwo() =
    println(
        Path.of("input", "day08", "input.txt").useLinesFromResource { lines ->
            val instructions: Map<Int, Instruction> = lines
                .instructions()
                .toMap()
            runBlocking {
                var timeout = TIMEOUT_MILLIS
                var value: Int?
                val computerVariantsToTry = instructions.computerVariantsToTry()
                do {
                    value = computerVariantsToTry
                        .findFirstTerminatedCorrectly(instructions.size, timeout)
                        ?.value
                    timeout += TIMEOUT_MILLIS
                } while(value == null)
                value
            }
        }
    )

private fun Map<Int, Instruction>.computerVariantsToTry() =
    this.asSequence()
        .map { (key, value) ->
            when (value) {
                is NopInstruction -> this.plus(key to JumpInstruction(value.parameter()))
                is JumpInstruction -> this.plus(key to NopInstruction(value.parameter()))
                else -> null
            }
        }
        .filterNotNull()
        .map(::Program)
        .map(::Computer)

private suspend fun Sequence<Computer>.findFirstTerminatedCorrectly(
    numberOfInstructions: Int,
    timeoutMillis: Long
): Accumulator? {
    val results = mutableListOf<Deferred<State?>?>()
    coroutineScope {
        for (computer in this@findFirstTerminatedCorrectly)
            results += async(Dispatchers.Default) {
                withTimeoutOrNull(timeoutMillis) {
                    computer.stateAfterExecution()
                }
            }
    }
    for (result in results.asSequence().filterNotNull()) {
        val state = result.await()
        if (state != null && state.pointer.value == numberOfInstructions) {
            return state.accumulator
        }
    }
    return null
}

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

private fun Sequence<Instruction>.toMap() =
    mapIndexed { index, instruction -> index to instruction }
        .toMap()

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

private class Computer(
    private val program: Program,
) {
    var state: State = State()
        private set

    fun step(): State? =
        when (val instruction = program[state.pointer.value]) {
            is Instruction -> {
                state = instruction.execute(state)
                state
            }
            else -> null
        }

    suspend fun run() {
        var instruction = program[state.pointer.value]
        while (instruction != null) {
            state = instruction.execute(state)
            instruction = program[state.pointer.value]
            yield()
        }
    }
}

private suspend fun Computer.stateAfterExecution(): State {
    run()
    return state
}

private class Program(
    private val instructions: Map<Int, Instruction>
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
    fun parameter(): Int
}

private class AccInstruction(
    private val delta: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        accumulator = state.accumulator.changeBy(delta),
        pointer = state.pointer.changeBy(1)
    )

    override fun parameter(): Int = delta
}

private class NopInstruction(
    private val parameter: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        pointer = state.pointer.changeBy(1)
    )

    override fun parameter(): Int = parameter
}

private class JumpInstruction(
    private val delta: Int
) : Instruction {
    override fun execute(state: State) = state.copy(
        pointer = state.pointer.changeBy(delta)
    )

    override fun parameter(): Int = delta
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