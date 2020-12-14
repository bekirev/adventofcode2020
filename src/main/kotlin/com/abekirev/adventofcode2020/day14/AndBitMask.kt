package com.abekirev.adventofcode2020.day14

class AndBitMask(private val mask: Long) : BitMask {
    override fun mask(value: Long): Long =
        mask and value
}
