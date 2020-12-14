package com.abekirev.adventofcode2020.day14

fun interface BitMask {
    fun mask(value: Long): Long
}

fun BitMask.andThen(bitMask: BitMask): BitMask = BitMask { value ->
    bitMask.mask(this@andThen.mask(value))
}
