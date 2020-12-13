package com.abekirev.adventofcode2020.day13

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day13", "input.txt").useLinesFromResource { lines ->
            val input = lines.toList()
            val startTime = input[0].toInt()
            val busPeriods = input[1].split(',').mapNotNull { it.toIntOrNull() }
            val (departTime, period) = firstNumberDividerPair(startTime, busPeriods)
            (departTime - startTime) * period
        }
    )

fun firstNumberDividerPair(startNumber: Int, dividers: Collection<Int>): NumberDividerPair {
    if (startNumber <= 0)
        throw IllegalArgumentException("Start number should be positive")
    if (dividers.isEmpty())
        throw IllegalArgumentException("dividers collection shouldn't be empty")
    return generateSequence(startNumber, 1::plus)
        .flatMap { numberAfter ->
            dividers.asSequence()
                .mapNotNull { divider ->
                    if (numberAfter % divider == 0) NumberDividerPair(numberAfter, divider)
                    else null
                }
        }
        .first()
}

data class NumberDividerPair(
    val number: Int,
    val divider: Int,
)