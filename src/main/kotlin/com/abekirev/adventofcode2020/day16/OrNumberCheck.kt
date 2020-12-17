package com.abekirev.adventofcode2020.day16

class OrNumberCheck(
    private val firstNumberCheck: NumberCheck,
    private val secondNumberCheck: NumberCheck,
) : NumberCheck {
    override fun check(number: Number): Boolean =
        number compliesWith firstNumberCheck || number compliesWith secondNumberCheck
}

infix fun NumberCheck.or(other: NumberCheck): NumberCheck =
    OrNumberCheck(this, other)
