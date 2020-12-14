package com.abekirev.adventofcode2020.day14

object IdentityMutatorMask : MutatorMask {
    override fun mutate(value: Long): Sequence<Long> = sequenceOf(value)
}
