package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

class SetMutatorMaskInstruction(
    private val mutatorMask: MutatorMask,
) : Instruction<InitializationProgramState<MutatorMask>> {
    override fun execute(state: InitializationProgramState<MutatorMask>): InitializationProgramState<MutatorMask> =
        state.copy(
            pointer = state.pointer + 1,
            mask = mutatorMask
        )
}