package com.abekirev.adventofcode2020.day14

class MapMemory<I, T> private constructor(
    private val map: Map<I, T>,
    private val initFunc: (I) -> T,
) : Memory<I, T> {
    constructor(initFunc: (I) -> T) : this(mapOf(), initFunc)

    override fun get(index: I): T = map[index] ?: initFunc(index)
    override fun set(index: I, value: T): Memory<I, T> = MapMemory(map.plus(index to value), initFunc)
    override fun set(values: Sequence<Pair<I, T>>) = MapMemory(map.plus(values), initFunc)
    override fun values(): Sequence<T> = map.values.asSequence()
}
