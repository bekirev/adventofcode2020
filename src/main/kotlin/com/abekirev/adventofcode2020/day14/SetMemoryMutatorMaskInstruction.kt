package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

class SetMemoryMutatorMaskInstruction(
    private val index: Long,
    private val value: Long,
) : Instruction<InitializationProgramState<MutatorMask>> {
    override fun execute(state: InitializationProgramState<MutatorMask>): InitializationProgramState<MutatorMask> =
        state.copy(
            pointer = state.pointer + 1,
            memory = state.memory.set(state.mask.mutate(index).map { it to value })
        )
}
