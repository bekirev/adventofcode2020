package com.abekirev.adventofcode2020.computer

interface Computer<S : State> {
    val state: S
    fun tick(): Computer<S>
}
