package com.abekirev.adventofcode2020.day14

class FloatingIndicesMutatorMask private constructor(
    private val bitMasks: List<BitMask>,
) : MutatorMask {
    constructor(
        floatingIndex: Int,
    ) : this(
        listOf(
            AndBitMask((1L shl floatingIndex).inv()),
            OrBitMask(1L shl floatingIndex),
        )
    )

    override fun mutate(value: Long): Sequence<Long> =
        bitMasks.asSequence().map { bitMask ->
            bitMask.mask(value)
        }
}
