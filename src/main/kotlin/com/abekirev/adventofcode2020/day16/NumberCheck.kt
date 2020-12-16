package com.abekirev.adventofcode2020.day16

fun interface NumberCheck {
    fun check(number: Int): Boolean
}

infix fun Int.compliesWith(numberCheck: NumberCheck): Boolean =
    numberCheck.check(this)

infix fun Int.notCompliesWith(numberCheck: NumberCheck): Boolean =
    !(this compliesWith numberCheck)
