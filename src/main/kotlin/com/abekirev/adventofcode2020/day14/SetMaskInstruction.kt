package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

class SetMaskInstruction(private val bitMask: BitMask) : Instruction<InitializationProgramState<BitMask>> {
    override fun execute(state: InitializationProgramState<BitMask>): InitializationProgramState<BitMask> =
        state.copy(
            pointer = state.pointer + 1,
            mask = bitMask,
        )
}
