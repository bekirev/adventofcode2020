package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Computer
import com.abekirev.adventofcode2020.computer.Instruction
import com.abekirev.adventofcode2020.computer.MapProgram
import com.abekirev.adventofcode2020.computer.SimpleComputer
import com.abekirev.adventofcode2020.computer.State
import com.abekirev.adventofcode2020.computer.stateAfterExecution
import com.abekirev.adventofcode2020.util.useLinesFromResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day14", "input.txt").useLinesFromResource { lines ->
            val instructions = lines.map(String::toInitializationProgramInstruction).toList()
            val computer: Computer<InitializationProgramState> = SimpleComputer(
                InitializationProgramState(),
                MapProgram(instructions),
            )
            val finalState = runBlocking { computer.stateAfterExecution() }
            instructions.asSequence()
                .filterIsInstance<SetMemoryUsingMaskInstruction>()
                .map(SetMemoryUsingMaskInstruction::index)
                .distinct()
                .map(finalState.memory::get)
                .sum()

        }
    )

private fun String.toInitializationProgramInstruction(): Instruction<InitializationProgramState> = when {
    startsWith("mem") -> toMemSetInstruction()
    startsWith("mask") -> toMaskSetInstruction()
    else -> throw IllegalArgumentException("Unknown instruction: $this")
}

private fun String.toMemSetInstruction(): SetMemoryUsingMaskInstruction {
    val (indexStr, valueStr) = substringAfter("mem").split(" = ")
    return SetMemoryUsingMaskInstruction(
        indexStr.substring(1 until indexStr.length - 1).toInt(),
        valueStr.toLong(),
    )
}

private fun String.toMaskSetInstruction(): SetMaskInstruction =
    SetMaskInstruction(substringAfter("mask = ").toBitMask())

private fun String.toBitMask(): BitMask {
    val zeroMask = when {
        contains('0') -> AndBitMask(replace('X', '1').toLong(2))
        else -> null
    }
    val oneMask = when {
        contains('1') -> OrBitMask(replace('X', '0').toLong(2))
        else -> null
    }
    return when {
        zeroMask != null -> when {
            oneMask != null -> zeroMask.andThen(oneMask)
            else -> zeroMask
        }
        oneMask != null -> oneMask
        else -> NoChangesMask
    }
}

private data class InitializationProgramState(
    override val pointer: Int = 0,
    val mask: BitMask = NoChangesMask,
    val memory: Memory<Int, Long> = MapMemory { 0 },
) : State

private interface Memory<I, T> {
    operator fun get(index: I): T
    fun set(index: I, value: T): Memory<I, T>
}

private class MapMemory<I, T> private constructor(
    private val map: Map<I, T>,
    private val initFunc: (I) -> T,
) : Memory<I, T> {
    constructor(initFunc: (I) -> T) : this(mapOf(), initFunc)

    override fun get(index: I): T = map[index] ?: initFunc(index)
    override fun set(index: I, value: T): Memory<I, T> = MapMemory(map.plus(index to value), initFunc)
}

private class SetMemoryUsingMaskInstruction(
    val index: Int,
    private val value: Long,
) : Instruction<InitializationProgramState> {
    override fun execute(state: InitializationProgramState): InitializationProgramState =
        state.copy(
            pointer = state.pointer + 1,
            memory = state.memory.set(index, state.mask.mask(value))
        )
}

private class SetMaskInstruction(private val bitMask: BitMask) : Instruction<InitializationProgramState> {
    override fun execute(state: InitializationProgramState): InitializationProgramState =
        state.copy(
            pointer = state.pointer + 1,
            mask = bitMask,
        )
}

private fun interface BitMask {
    fun mask(value: Long): Long
}

private object NoChangesMask : BitMask {
    override fun mask(value: Long): Long = value
}

private fun BitMask.andThen(bitMask: BitMask): BitMask = BitMask { value ->
    bitMask.mask(this@andThen.mask(value))
}

private class AndBitMask(private val mask: Long) : BitMask {
    override fun mask(value: Long): Long =
        mask and value
}

private class OrBitMask(private val mask: Long) : BitMask {
    override fun mask(value: Long): Long =
        mask or value
}
