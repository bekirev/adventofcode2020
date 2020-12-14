package com.abekirev.adventofcode2020.computer

class MapProgram<S : State>(
    private val instructionMap: Map<Int, Instruction<S>>,
) : Program<S> {
    constructor(instructions: List<Instruction<S>>) : this(instructions
        .asSequence()
        .mapIndexed { index, instruction -> index to instruction }
        .toMap()
    )

    override fun get(pointer: Int): Instruction<S> =
        instructionMap[pointer] ?: throw IndexOutOfBoundsException("Requested index: $pointer while size is ${instructionMap.size}")
}