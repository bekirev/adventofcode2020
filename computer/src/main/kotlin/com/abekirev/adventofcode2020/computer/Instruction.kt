package com.abekirev.adventofcode2020.computer

interface Instruction<S : State> {
    fun execute(state: S): S
}