package com.abekirev.adventofcode2020.day14

interface Memory<I, T> {
    operator fun get(index: I): T
    fun set(index: I, value: T): Memory<I, T>
    fun set(values: Sequence<Pair<I, T>>): Memory<I, T>
    fun values(): Sequence<T>
}
