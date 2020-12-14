package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

interface InitializationProgramInstructionFactory<M> {
    fun setMaskInstruction(mask: String): Instruction<InitializationProgramState<M>>
    fun memSetInstruction(index: Long, value: Long): Instruction<InitializationProgramState<M>>
}
