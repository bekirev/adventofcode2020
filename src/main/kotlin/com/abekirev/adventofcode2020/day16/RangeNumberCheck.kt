package com.abekirev.adventofcode2020.day16

class RangeNumberCheck(
    private val range: IntRange,
) : NumberCheck {
    override fun check(number: Int): Boolean =
        number in range
}
