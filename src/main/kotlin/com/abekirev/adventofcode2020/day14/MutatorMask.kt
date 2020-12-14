package com.abekirev.adventofcode2020.day14

fun interface MutatorMask {
    fun mutate(value: Long): Sequence<Long>
}

fun MutatorMask.andThen(other: MutatorMask): MutatorMask = MutatorMask { value ->
    mutate(value).flatMap(other::mutate)
}
