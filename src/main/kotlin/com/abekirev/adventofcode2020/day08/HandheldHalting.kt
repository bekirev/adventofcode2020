package com.abekirev.adventofcode2020.day08

import com.abekirev.adventofcode2020.computer.Computer
import com.abekirev.adventofcode2020.computer.Instruction
import com.abekirev.adventofcode2020.computer.MapProgram
import com.abekirev.adventofcode2020.computer.SimpleComputer
import com.abekirev.adventofcode2020.computer.State
import com.abekirev.adventofcode2020.util.useLinesFromResource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
            SimpleComputer(
                GameConsoleState(),
                MapProgram(
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
            val instructions: Map<Int, GameConsoleInstruction> = lines
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
                } while (value == null)
                value
            }
        }
    )

private fun Map<Int, GameConsoleInstruction>.computerVariantsToTry() =
    this.asSequence()
        .map { (key, value) ->
            when (value) {
                is NopInstruction -> this.plus(key to JumpInstruction(value.parameter()))
                is JumpInstruction -> this.plus(key to NopInstruction(value.parameter()))
                else -> null
            }
        }
        .filterNotNull()
        .map(::MapProgram)
        .map { program ->
            SimpleComputer(GameConsoleState(), program)
        }

private suspend fun Sequence<Computer<GameConsoleState>>.findFirstTerminatedCorrectly(
    numberOfInstructions: Int,
    timeoutMillis: Long,
): Accumulator? {
    val results = mutableListOf<Deferred<GameConsoleState?>>()
    coroutineScope {
        for (computer in this@findFirstTerminatedCorrectly)
            results += async(Dispatchers.Default) {
                withTimeoutOrNull(timeoutMillis) {
                    computer.stateAfterExecution()
                }
            }
    }
    for (result in results) {
        val state = result.await()
        if (state != null && state.pointer == numberOfInstructions) {
            return state.accumulator
        }
    }
    return null
}

private fun Computer<GameConsoleState>.runUntilInstructionExecutionRepeats(): GameConsoleState? {
    val executedLines = mutableSetOf<Int>()
    var computer: Computer<GameConsoleState> = this
    var stateBeforeExecutionRepeats: GameConsoleState = computer.state
    while (true) {
        executedLines += computer.state.pointer
        try {
            computer = computer.tick()
            if (computer.state.pointer in executedLines)
                return stateBeforeExecutionRepeats
            stateBeforeExecutionRepeats = computer.state
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
    }
}

private fun Sequence<String>.instructions() =
    map(String::toInstruction)

private fun Sequence<GameConsoleInstruction>.toMap() =
    mapIndexed { index, instruction -> index to instruction }
        .toMap()

private fun String.toInstruction(): GameConsoleInstruction {
    val (type, parameterStr) = split(" ")
    val parameter = parameterStr.toInt()
    return when (type) {
        "acc" -> AccInstruction(parameter)
        "jmp" -> JumpInstruction(parameter)
        "nop" -> NopInstruction(parameter)
        else -> throw IllegalArgumentException("Unknown instruction: $this")
    }
}

private suspend fun <S : State> Computer<S>.run(): Computer<S> {
    var computer = this
    while (true) {
        try {
            computer = computer.tick()
        } catch (e: IndexOutOfBoundsException) {
            break
        }
        yield()
    }
    return computer
}

private suspend fun Computer<GameConsoleState>.stateAfterExecution(): GameConsoleState {
    return run().state
}

private data class GameConsoleState(
    override val pointer: Int = 0,
    val accumulator: Accumulator = Accumulator(0),
) : State

private interface GameConsoleInstruction : Instruction<GameConsoleState> {
    override fun execute(state: GameConsoleState): GameConsoleState
    fun parameter(): Int
}

private class AccInstruction(
    private val accDelta: Int,
) : GameConsoleInstruction {
    override fun execute(state: GameConsoleState) = state.copy(
        accumulator = state.accumulator.changeBy(accDelta),
        pointer = state.pointer + 1
    )

    override fun parameter(): Int = accDelta
}

private class NopInstruction(
    private val parameter: Int,
) : GameConsoleInstruction {
    override fun execute(state: GameConsoleState) = state.copy(
        pointer = state.pointer + 1
    )

    override fun parameter(): Int = parameter
}

private class JumpInstruction(
    private val pointerDelta: Int,
) : GameConsoleInstruction {
    override fun execute(state: GameConsoleState) = state.copy(
        pointer = state.pointer + pointerDelta
    )

    override fun parameter(): Int = pointerDelta
}

private data class Accumulator(
    val value: Int,
) {
    fun changeBy(delta: Int) = Accumulator(value + delta)
}