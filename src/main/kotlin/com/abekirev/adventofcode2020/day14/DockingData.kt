package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Computer
import com.abekirev.adventofcode2020.computer.Instruction
import com.abekirev.adventofcode2020.computer.MapProgram
import com.abekirev.adventofcode2020.computer.SimpleComputer
import com.abekirev.adventofcode2020.computer.stateAfterExecution
import com.abekirev.adventofcode2020.util.useLinesFromResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        Path.of("input", "day14", "input.txt").useLinesFromResource { lines ->
            val computer: Computer<InitializationProgramState<BitMask>> = SimpleComputer(
                InitializationProgramState(NoChangesMask),
                MapProgram(
                    lines.map { line ->
                        line.toInitializationProgramInstruction(
                            SetMaskedInitializationProgramInstructionFactory
                        )
                    }
                ),
            )
            val finalState = runBlocking { computer.stateAfterExecution() }
            finalState.memory.values().sum()
        }
    )

private fun partTwo() =
    println(
        Path.of("input", "day14", "input.txt").useLinesFromResource { lines ->
            val computer: Computer<InitializationProgramState<MutatorMask>> = SimpleComputer(
                InitializationProgramState(IdentityMutatorMask),
                MapProgram(
                    lines.map { line ->
                        line.toInitializationProgramInstruction(
                            FloatingIndicesInitializationProgramInstructionFactory
                        )
                    }
                ),
            )
            val finalState = runBlocking { computer.stateAfterExecution() }
            finalState.memory.values().sum()
        }
    )

private fun <M> String.toInitializationProgramInstruction(
    initializationProgramInstructionFactory: InitializationProgramInstructionFactory<M>,
): Instruction<InitializationProgramState<M>> {
    fun String.toMemSetInstruction(
        factoryMethod: (Long, Long) -> Instruction<InitializationProgramState<M>>,
    ): Instruction<InitializationProgramState<M>> {
        val (indexStr, valueStr) = substringAfter("mem").split(" = ")
        return factoryMethod(
            indexStr.substring(1 until indexStr.length - 1).toLong(),
            valueStr.toLong(),
        )
    }

    fun String.toMaskSetInstruction(
        factoryMethod: (String) -> Instruction<InitializationProgramState<M>>,
    ): Instruction<InitializationProgramState<M>> =
        factoryMethod(substringAfter("mask = "))

    return when {
        startsWith("mem") -> toMemSetInstruction(initializationProgramInstructionFactory::memSetInstruction)
        startsWith("mask") -> toMaskSetInstruction(initializationProgramInstructionFactory::setMaskInstruction)
        else -> throw IllegalArgumentException("Unknown instruction: $this")
    }
}
