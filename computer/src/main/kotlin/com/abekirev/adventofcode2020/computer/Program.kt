package com.abekirev.adventofcode2020.computer

interface Program<S : State> {
    operator fun get(pointer: Int): Instruction<S>
}