package com.abekirev.adventofcode2020.computer

import kotlinx.coroutines.yield

interface Computer<S : State> {
    val state: S
    fun tick(): Computer<S>
}

suspend fun <S : State> Computer<S>.run(): Computer<S> {
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

suspend fun <S : State> Computer<S>.stateAfterExecution(): S {
    return run().state
}
