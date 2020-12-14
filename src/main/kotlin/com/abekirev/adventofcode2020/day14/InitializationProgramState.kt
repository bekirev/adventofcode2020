package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.State

data class InitializationProgramState<M>(
    override val pointer: Int = 0,
    val mask: M,
    val memory: Memory<Long, Long> = MapMemory { 0 },
) : State {
    constructor(initMask: M) : this(mask = initMask)
}
