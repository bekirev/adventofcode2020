package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

class SetMemoryBitMaskInstruction(
    private val index: Long,
    private val value: Long,
) : Instruction<InitializationProgramState<BitMask>> {
    override fun execute(state: InitializationProgramState<BitMask>): InitializationProgramState<BitMask> =
        state.copy(
            pointer = state.pointer + 1,
            memory = state.memory.set(index, state.mask.mask(value))
        )
}
