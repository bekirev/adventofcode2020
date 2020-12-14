package com.abekirev.adventofcode2020.day14

class OrBitMask(private val mask: Long) : BitMask {
    override fun mask(value: Long): Long =
        mask or value
}
