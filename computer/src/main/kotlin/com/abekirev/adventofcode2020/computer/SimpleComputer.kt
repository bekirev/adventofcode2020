package com.abekirev.adventofcode2020.computer

class SimpleComputer<S : State>(
    override val state: S,
    private val program: Program<S>,
) : Computer<S> {
    override fun tick(): Computer<S> =
        SimpleComputer(
            program[state.pointer].execute(state),
            program
        )
}