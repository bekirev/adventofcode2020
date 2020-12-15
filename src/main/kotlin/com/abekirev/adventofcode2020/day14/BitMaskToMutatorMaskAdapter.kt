package com.abekirev.adventofcode2020.day14

class BitMaskToMutatorMaskAdapter(
    private val bitMask: BitMask,
) : MutatorMask {
    override fun mutate(value: Long): Sequence<Long> =
        sequenceOf(bitMask.mask(value))
}
